'use strict';
opsdataapp.factory('opsdataFactory', function ($http) {
	var STORAGE_ID = 'todos-angularjs';

	return {
			getZeroCounts: function () {
					var url = "/opsData/getZeroCounts";
					return $http.get(url);
			},
			getOneCounts: function () {
					var url = "/opsData/getOneCounts";
					return $http.get(url);
			}
	};
});
