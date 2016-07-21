var mainApp = angular.module('Main3App', ['tc.chartjs','OrgSpaceAppApp'], function($interpolateProvider) {
      $interpolateProvider.startSymbol('{*{');
      $interpolateProvider.endSymbol('}*}');
  });
