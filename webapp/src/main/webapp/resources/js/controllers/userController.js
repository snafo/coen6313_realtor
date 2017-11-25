var userCtrl = angular.module('app.controllers')；

    userCtrl.config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Request-With'];
    }]);

    userCtrl.Controller('userController', function($scope, $http){
        $scope.base_Url = 'http://localhost:8080/rest/user/';

        $http.get($scope.base_Url+ 'findall').success(function (response) {
                $scope.result = response.user;
            });

        $scope.add = function(){
            $http.post($scope.base_Url + 'create', $scope.user).success(function (response) {
                $http.get($scope.base_Url+ 'findall').success(function (response) {
                    $scope.result = response.user;

                });
            });
        };

        $scope.del = function(id){
            var result = confirm('Are you sure? ');
            if(result === true){
                $http.delete($scope.base_Url+'delete/'+id).success(function(response){
                    alert('Done');
                });
            }
        };

        $scope.edit = function(user){
            $scope.user = user;
        };

        $scope.update = function(){
            $http.put($scope.base_Url + 'edit', $scope.user).success(function (response) {
                alert('Done');
            })
        };

    });