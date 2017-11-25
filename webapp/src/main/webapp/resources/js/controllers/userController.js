angular.module('app.controllers')
   .controller('UserController', function($scope, QueryServices){
    $scope.userParam = {};

    $scope.createUser = function(param){
        QueryServices.createUser(param).then(function (result){
            console.log(result.message);
        });
    };
});