var mainApp = angular.module('MainApp', ['tc.chartjs','AppsApp', 'UsersApp', 'EventsApp', 'AppEventsApp'], function($interpolateProvider) {
      $interpolateProvider.startSymbol('{*{');
      $interpolateProvider.endSymbol('}*}');
  });
