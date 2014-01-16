'use strict';

/* Controllers */

angular.module('huutoNetApp.controllers', []).
  controller('HuutoNetItemsCtrl', ['$scope', '$http', 'HuutoNet', function($scope, $http, HuutoNet) {
	  $http({method: 'GET', url: '/service/itemids/'}).
		success(function(data, status, headers, config) {
		     $scope.items = []
			 for(var j = 0; j < data.length; j++){
				 $scope.items.push({id: data[j]});
			 }
			 
			 for(var i = 0; i < $scope.items.length; i++){
				 	//alert($scope.items[i].id);
				  $http({method: 'GET', url: '/service/items/'+$scope.items[i].id, params: {index: i}}).
				  		success(function(data, status, headers, config) {
				  			$scope.items[config.params.index] = data;
				  });
			  }
		});
  }]);