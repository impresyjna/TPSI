"use strict";
var viewModel = {
    students: ko.observableArray(),
    grades: ko.observableArray(),
    courses: ko.observableArray()
};

viewModel.init = function () {
    viewModel.getAllStudents();
    return viewModel;
};

viewModel.getAllStudents = function () {
    viewModel.students.removeAll();
    $.ajax({
        method: "GET",
        url: "http://localhost:8000/students",
        dataType: "json",
        success: function (data) {
            data.forEach(function (element, index, array) {
                var student = ko.mapping.fromJS(element);
                viewModel.students.push(student);
            });
        }
    });
};

ko.applyBindings(viewModel.init());