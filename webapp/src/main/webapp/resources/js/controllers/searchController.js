
angular
    .module('app.controllers')
    .controller('SearchController', function ($scope, $facebook) {

        $scope.$on('fb.auth.authResponseChange', function() {
            $scope.status = $facebook.isConnected();
            if($scope.status) {
                $facebook.api('/me').then(function(user) {
                    $scope.user = user;
                });
            }
        });

        $scope.loginToggle = function() {
            if($scope.status) {
                $facebook.logout();
            } else {
                $facebook.login();
            }
        };

});
