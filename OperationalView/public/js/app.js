'use strict';

/**
 * @type {angular.Module}
 */
var opsdataapp = angular.module('opsAnalyticsApp', ['ngRoute','chart.js'])
	.config(function ($routeProvider) {
		$routeProvider.when('/', {
			controller: 'OpsDataCtrl',
			templateUrl: 'opsdata-index.html'
		}).when('/:status', {
			controller: 'OpsDataCtrl',
			templateUrl: 'opsdata-index.html'
		}).otherwise({
			redirectTo: '/'
		});
	});
