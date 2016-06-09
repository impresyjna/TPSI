"use strict";

var hostAddress = "http://localhost:8000/";

var dataFromServer = function (url, idAttr) {
    var self = ko.observableArray();
    self.url = url;
    var isItGrade = false;

    self.get = function () {
        if (self.sub != undefined) {
            self.sub.dispose();
        }
        self.removeAll();
        $.ajax({
            method: "GET",
            url: self.url,
            dataType: "json",
            success: function (data) {
                data.forEach(function (element, index, array) {
                    var object = ko.mapping.fromJS(element, {ignore: ["link"]});
                    object.links = [];

                    if ($.isArray(element.link)) {
                        element.link.forEach(function (link) {
                            object.links[link.params.rel] = link.href;
                        });
                    } else {
                        object.links[element.link.params.rel] = element.link.href;
                    }

                    self.push(object);
                    ko.computed(function () {
                        return ko.toJSON(object);
                    }).subscribe(function () {
                        self.updateRequest(object);
                    });
                })

                self.sub = self.subscribe(function(changes) {
                    changes.forEach(function(change) {
                        if(change.status == 'added' && isItGrade==false) {
                            self.createRequest(change.value,self.url);
                        }
                        if(change.status == 'deleted') {
                            self.deleteRequest(change.value);
                        }
                    });
                }, null, "arrayChange");
            }
        });
    }

    self.createRequest = function(object, url) {
        $.ajax({
            url: url,
            dataType: "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(object),
            method: "POST",
            success: function(data) {
                var response = ko.mapping.fromJS(data);

                object[idAttr](response[idAttr]());
                object.links = [];

                if($.isArray(data.link)) {
                    data.link.forEach(function(link) {
                        object.links[link.params.rel] = link.href;
                    });
                } else {
                    object.links[data.link.params.rel] = data.link.href;
                }

                ko.computed(function() {
                    return ko.toJSON(object);
                }).subscribe(function() {
                    self.updateRequest(object);
                });
            }
        });
    }

    self.updateRequest = function(object) {
        $.ajax({
            url: object.links['self'],
            dataType: "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(object, { ignore: ["links"] }),
            method: "PUT"
        });
    }

    self.newGrade = function (form) {
        isItGrade = true;
        var data = {};
        $(form).serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        //self.push(ko.mapping.fromJS(data));
        $(form).each(function() {
            this.reset();
        });
        self.createRequest(ko.mapping.fromJS(data, { ignore: ["student.index", "courseId"] }), hostAddress + "students/" + data['student.index'] + "/courses/" + data['courseId'] + "/grades");
        isItGrade = false;

    }

    self.new = function(form) {
        var data = {};
        $(form).serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        self.push(ko.mapping.fromJS(data));
        $(form).each(function() {
            this.reset();
        });
    }

    self.deleteRequest = function(object) {
        $.ajax({
            url: object.links['self'],
            method: "DELETE"
        });
    }

    self.delete = function() {
        self.remove(this);
        self.deleteRequest(this);
    }

    return self;
}

function viewModel() {
    var self = this;

    self.students = new dataFromServer(hostAddress + "students", "index");
    self.students.getGrades = function () {
        window.location = "#grades";
        self.grades.selectedStudent(this.index());
        self.grades.selectedCourse(null);
        self.grades.url = this.links["grades"];
        self.grades.get();
    }
    self.students.get();

    self.courses = new dataFromServer(hostAddress + "courses", "courseId");
    self.courses.getGrades = function () {
        window.location = "#grades";
        self.grades.selectedStudent(null);
        self.grades.selectedCourse(this.id());
        self.grades.url = this.links["grades"];
        self.grades.get();
    }
    self.courses.get();

    self.grades = new dataFromServer(hostAddress + "grades", "id");
    self.grades.selectedCourse = ko.observable();
    self.grades.selectedStudent = ko.observable();
}

var model = new viewModel();

ko.applyBindings(model);