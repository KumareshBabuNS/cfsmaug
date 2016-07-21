var mainApp = angular.module('Main2App', ['tc.chartjs', 'AppEventsApp', 'FailedAppsApp'], function($interpolateProvider) {
      $interpolateProvider.startSymbol('{*{');
      $interpolateProvider.endSymbol('}*}');
  });
