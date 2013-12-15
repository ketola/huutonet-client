'use strict';

/* Controllers */

angular.module('huutoNetApp.controllers', []).
  controller('HuutoNetItemsCtrl', ['$scope','$http', function($scope, $http) {  
	  var url = '/service/items/?callback=JSON_CALLBACK';
	    $http.jsonp(url).success(function(data) {
	        $scope.items = data;
	    });
  }]);