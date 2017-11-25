// Default colors;
var brandPrimary =  '#20a8d8';
var brandSuccess =  '#4dbd74';
var brandInfo =     '#63c2de';
var brandWarning =  '#f8cb00';
var brandDanger =   '#f86c6b';

var grayDark =      '#2a2c36';
var gray =          '#55595c';
var grayLight =     '#818a91';
var grayLighter =   '#d1d4d7';
var grayLightest =  '#f8f9fa';

angular
    .module('app', [
        'ui.router',
        'ui.bootstrap',
        'angular-momentjs',
        'daterangepicker',
        'restangular',
        'ui.grid',
        'ui.grid.pagination',
        'ui.grid.resizeColumns',
        'ui.grid.moveColumns',
        'ui.grid.expandable',
        'ui.grid.selection',
        'ui.grid.edit',
        'ui.grid.rowEdit',
        'ui.grid.cellNav',
        'ui.grid.autoResize',
        'app.services',
        'app.controllers',
        'app.routing',
        'ngFacebook',
        'ngMap'
    ])
    .config(function($facebookProvider){
        $facebookProvider.setAppId('174897076426435');
    })
    .run(['$rootScope', '$state', '$stateParams', 'Restangular', function($rootScope, $state, $stateParams, Restangular) {
        $rootScope.$on('$stateChangeSuccess',function(){
            document.body.scrollTop = document.documentElement.scrollTop = 0;
        });
        Restangular.setBaseUrl('rest/api/');
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;

        (function(){


        }());
    }]);


// angular
//     .module('app', ['ngRoute', 'ngCookies'])
//     .config(config)
//     .run(run);
//
// config.$inject = ['$routeProvider', '$locationProvider'];
// function config($routeProvider, $locationProvider) {
//     $routeProvider
//         .when('/', {
//             controller: 'HomeController',
//             templateUrl: 'home/home.view.html',
//             controllerAs: 'vm'
//         })
//
//         .when('/login', {
//             controller: 'LoginController',
//             templateUrl: 'login/login.view.html',
//             controllerAs: 'vm'
//         })
//
//         .when('/register', {
//             controller: 'RegisterController',
//             templateUrl: 'register/register.view.html',
//             controllerAs: 'vm'
//         })
//
//         .otherwise({ redirectTo: '/login' });
// }
//
// run.$inject = ['$rootScope', '$location', '$cookies', '$http'];
// function run($rootScope, $location, $cookies, $http) {
//     // keep user logged in after page refresh
//     $rootScope.globals = $cookies.getObject('globals') || {};
//     if ($rootScope.globals.currentUser) {
//         $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
//     }
//
//     $rootScope.$on('$locationChangeStart', function (event, next, current) {
//         // redirect to login page if not logged in and trying to access a restricted page
//         var restrictedPage = $.inArray($location.path(), ['/login', '/register']) === -1;
//         var loggedIn = $rootScope.globals.currentUser;
//         if (restrictedPage && !loggedIn) {
//             $location.path('/login');
//         }
//     });
// }
