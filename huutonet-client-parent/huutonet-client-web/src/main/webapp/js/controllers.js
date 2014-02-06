'use strict';

/* Controllers */

angular.module('huutoNetApp.controllers', []).
  controller('HuutoNetItemsCtrl', ['$scope', '$http', 'HuutoNet', function($scope, $http, HuutoNet) {
	  $scope.filters = {ALL : {}, AUCTION: {type : 'AUCTION'}, BUY_NOW: {type: 'BUY_NOW'} };
	  $scope.itemsFilter = $scope.filters.ALL;
	  $scope.items = [];
	  $scope.loadedSets = 0;
	  $scope.setSize = 20;
	  
	  $scope.loadItems = function(){
		  $scope.loadedSets++;
		  $http({method: 'GET', url: '/service/itemids/'+ $scope.loadedSets + '?pageSize=' + $scope.setSize}).
			success(function(data, status, headers, config) {
				
				if(data.length === 0){
					 return;
				}
				
				for(var j = 0; j < data.length; j++){
					 $scope.items.push({id: data[j]});
				 }
				 
				 for(var i = (($scope.loadedSets - 1) * $scope.setSize); i < $scope.items.length; i++){
					 $http({method: 'GET', url: '/service/items/'+$scope.items[i].id, params: {index: i}}).
					  		success(function(data, status, headers, config) {
					  			$scope.items[config.params.index] = data;
					  });
				 }
				 
				 // if previous call returned full set, load more items
				 if(data.length === $scope.setSize){
					 $scope.loadItems();
				 }
			});
	  };
	 
	  $scope.loadItems();
  }]);