'use strict';
var huutoNetServices = angular.module('huutoNetApp.services', [ 'ngResource' ]);

huutoNetServices.factory('HuutoNet', [ '$resource', function($resource) {
	//return $resource('/example-response.json', {}, {
		return $resource('/service/items', {}, {	
		query : {
			method : 'GET',
			isArray : true
		}
	});
} ]);