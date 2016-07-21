angular.module('FailedAppsApp', [])
  .controller('FailedAppsController', function($scope, $http, $interval, $timeout, $window) {
    var events = this;
    var refreshInterval = 300000;//TODO ALEX was 5000
    var eventsInfoURI = "/v2/info/failedapps";
    var orgSpaceAppsURI = "/v2/info/orgspaceapps";    	 
    var messaging = {
      unavailable: "** Service Unavailable **",
      empty: "-",
      zero: 0
    };

    $interval( function(){ events.getEventsInfo(); }, refreshInterval, 0, true);

    $timeout(function () {
      events.getEventsInfo();
    }, 1);

    events.getEventsInfo = function() {
      var responsePromise = $http.get(eventsInfoURI);
      responsePromise.success(function(data, status, headers, config) {
        $scope.failed = data;
        $scope.userInfoStatus = "";
      });

      responsePromise.error(function(data, status, headers, config) {
        $scope.failed = {};
        $scope.userInfoStatus = messaging.unavailable;
        console.log(status, data);
      });
      responsePromise = $http.get(orgSpaceAppsURI);
      responsePromise.success(function(data, status, headers, config) {
        $scope.urlByApp = data.urlByApp;
      });

      responsePromise.error(function(data, status, headers, config) {
        $scope.failed = {};
        $scope.urlByApp = {};
        $scope.userInfoStatus = messaging.unavailable;
        console.log(status, data);
      });
    };
  });
