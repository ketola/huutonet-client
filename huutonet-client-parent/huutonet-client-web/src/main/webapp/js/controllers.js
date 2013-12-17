'use strict';

/* Controllers */

angular.module('huutoNetApp.controllers', []).
  controller('HuutoNetItemsCtrl', ['$scope','HuutoNet', function($scope, HuutoNet) {
	  $scope.items = HuutoNet.query();
  }]);