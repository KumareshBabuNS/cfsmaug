angular.module('AppEventsApp', [])
  .controller('AppEventsController', function($scope, $http, $interval, $timeout, $window) {
    var events = this;
    var refreshInterval = 30000;//TODO ALEX was 5000
    var eventsInfoURI = "/v2/info/appevents";
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
        $scope.events = data;
        $scope.userInfoStatus = "";
      });

      responsePromise.error(function(data, status, headers, config) {
        $scope.events = {};
        $scope.userInfoStatus = messaging.unavailable;
        console.log(status, data);
      });
      
      responsePromise = $http.get(orgSpaceAppsURI);
      responsePromise.success(function(data, status, headers, config) {
        $scope.osa = data;
      });

      responsePromise.error(function(data, status, headers, config) {
        $scope.events = {};
        $scope.urlByApp = {};
        $scope.userInfoStatus = messaging.unavailable;
        console.log(status, data);
      });
      
      
    };
  });
