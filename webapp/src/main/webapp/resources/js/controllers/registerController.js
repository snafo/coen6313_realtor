angular
    .module('app.controllers')
    .controller('RegisterController', function ($scope,$location, QueryServices) {
        $scope.userParam = {};

        $scope.createUser = function(param){
            QueryServices.createUser(param).then(function (result){
                if(result.code === 1){
                    console.log(result.message);
                    $location.path('/login');
                } else{
                    alert(result.message);
                }
            });
        };
    });