
angular.module('app.controllers')
    .controller('LoginController', function LoginController($location, AuthenticationService, FlashService) {
        var vm = this;

        vm.login = login;

        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
        })();

        function login() {
            vm.dataLoading = true;
            AuthenticationService.Login(vm.name, vm.password, function (response) {
                if (response.success) {
                    AuthenticationService.SetCredentials(vm.name, vm.password);
                    $location.path('/searchResult');
                } else {
                    FlashService.Assertion(response.message);
                    vm.dataLoading = false;
                }
            });
        }
    });
