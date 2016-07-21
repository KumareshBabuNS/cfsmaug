angular.module('OrgSpaceAppApp', [])
  .controller('OSAController', function($scope, $http, $interval, $timeout, $window) {
    var apps = this;
    var refreshInterval = 300000;
    var appsInfoURI = "/v2/info/orgspaceapps";
    var messaging = {
      unavailable: "** Service Unavailable **",
      empty: "-",
      zero: 0
    };

    $interval( function(){ apps.getAppsInfo(); }, refreshInterval, 0, true);

    $timeout(function () {
      apps.getAppsInfo();
    }, 1);

    // Chart.js Options
    $scope.options =  {
      // Sets the chart to be responsive
      responsive: true,
      //Boolean - Whether we should show a stroke on each segment
      segmentShowStroke : true,
      //String - The colour of each segment stroke
      segmentStrokeColor : '#fff',
      //Number - The width of each segment stroke
      segmentStrokeWidth : 2,
      //Number - The percentage of the chart that we cut out of the middle
      percentageInnerCutout : 50, // This is 0 for Pie charts
      //Number - Amount of animation steps
      animationSteps : 100,
      //String - Animation easing effect
      animationEasing : 'easeOutBounce',
      //Boolean - Whether we animate the rotation of the Doughnut
      animateRotate : false,
      //Boolean - Whether we animate scaling the Doughnut from the centre
      animateScale : false,
      //String - A legend template
      //legendTemplate : '<ul class="tc-chart-js-legend"><% for (var i=0; i<segments.length; i++){%><li><span style="background-color:<%=segments[i].fillColor%>"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>'
    };

    apps.setDonutUX = function(data) {
    	// AI
    	$scope.dataAI = [];
    	$scope.dataMemory = [];
    	$scope.dataSpaces = [];
    	$scope.dataServices = [];
    	Object.keys(data.spacesByOrg).forEach(function(org) {
    		var map = {};
    		var mapMem = {};
    		var mapSpace = {};
    		var mapServices = {};
    		map.label = data.orgs[org];
    		mapMem.label = map.label; 
    		mapSpace.label = map.label;
    		mapServices.label = map.label;
    		map.color = "#" + Math.random().toString(16).slice(2, 8);
    		mapMem.color = map.color;
    		mapSpace.color = map.color;
    		mapServices.color = map.color;
    		map.value = 0;
    		mapMem.value = 0;
    		mapSpace.value = data.spacesByOrg[org].length;
    		mapServices.value = 0;
    		if (data.spacesByOrg[org])//can have no space
    		data.spacesByOrg[org].forEach(function(space) {
    			//services
    			if (data.servicesBySpace[space]) mapServices.value += data.servicesBySpace[space].length;
    			// apps
    			if (data.appsBySpace[space])//can have no app
    			data.appsBySpace[space].forEach(function(a) {
    				map.value += a.ai;
    				mapMem.value += a.mb;
    			});
    		});
    		$scope.dataAI.push(map);
    		$scope.dataMemory.push(mapMem);
    		$scope.dataSpaces.push(mapSpace);
    		$scope.dataServices.push(mapServices);
    	});
    }

    apps.getAppsInfo = function() {
      var responsePromise = $http.get(appsInfoURI);
      responsePromise.success(function(data, status, headers, config) {
        $scope.spaceCount = data.spaceCount || messaging.zero;
        $scope.userInfoStatus = "";
        apps.setDonutUX(data);
      });

      responsePromise.error(function(data, status, headers, config) {
        $scope.spaceCount = messaging.empty;        
        $scope.userInfoStatus = messaging.unavailable;
        console.log(status, data);
      });
    };
  });
