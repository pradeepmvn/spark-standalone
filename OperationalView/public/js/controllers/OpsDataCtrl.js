'use strict';
/**
 * The main controller for the app.
 */
opsdataapp.controller('OpsDataCtrl', function TodoCtrl($scope, $routeParams, opsdataFactory,$interval) {
  $scope.data = [];

//There are two options to load data
//1. MAke a call to database(rethinkdb) api at a frequency
//2. Or Create a callback functino using change API on rethink DB. (Optimal way.but TODO for now)
  $interval(function() {
    opsdataFactory.getZeroCounts().success(function(zeroCount) {
        //now get one counts
        opsdataFactory.getOneCounts().success(function(oneCount) {
            $scope.data = [parseInt(zeroCount),parseInt(oneCount)] ;
            console.log($scope.data);
          }).error(function(error) {
              alert("Failed to load counts");
          });
      }).error(function(error) {
          alert("Failed to load counts");
      });
    }, 1000);
    $scope.labels = ["Probaility for Failure","Good Systems"];
});
