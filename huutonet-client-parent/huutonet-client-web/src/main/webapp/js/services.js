'use strict';
var huutoNetServices = angular.module('huutoNetApp.services', []);

huutoNetServices.factory('HuutoNet', [ '$http', function($http) {
	//return $resource('/example-response.json', {}, {
	return $http.jsonp('/service/items?callback=JSON_CALLBACK');;
	
		
} ]);