var app = angular.module('app', ['ngRoute','ngResource']);
app.config(function($routeProvider){
    $routeProvider
        .when('/users',{
            templateUrl: '/tmpviews/users.html',
            controller: 'usersController'
        })
        .when('/roles',{
            templateUrl: '/tmpviews/roles.html',
            controller: 'rolesController'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});