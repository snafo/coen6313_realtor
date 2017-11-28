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
        'ngMap',
        'ngCookies',
        'angularUtils.directives.dirPagination'
    ])
    .config(function($facebookProvider){
        $facebookProvider.setAppId('174897076426435');
    })
    .run(['$rootScope', '$state', '$stateParams', 'Restangular', '$location', '$cookies', '$http', function($rootScope, $state, $stateParams, Restangular, $location, $cookies, $http){
        $rootScope.$on('$stateChangeSuccess',function(){
            document.body.scrollTop = document.documentElement.scrollTop = 0;
        });
        Restangular.setBaseUrl('rest/api/');
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;

        if (document.getElementById('facebook-jssdk')) {return;}

        // Get the first script element, which we'll use to find the parent node
        var firstScriptElement = document.getElementsByTagName('script')[0];

        // Create a new script element and set its id
        var facebookJS = document.createElement('script');
        facebookJS.id = 'facebook-jssdk';

        // Set the new script's source to the source of the Facebook JS SDK
        facebookJS.src = '//connect.facebook.net/en_US/all.js';

        // Insert the Facebook JS SDK into the DOM
        firstScriptElement.parentNode.insertBefore(facebookJS, firstScriptElement);

        // keep user logged in after page refresh
        $rootScope.globals = $cookies.getObject('globals') || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common.Authorization = 'Basic ' + $rootScope.globals.currentUser.authdata;
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            // redirect to search page if not logged in and trying to access a restricted page
            var restrictedPage = $.inArray($location.path(), ['/search','/login', '/register']) === -1;
            var loggedIn = $rootScope.globals.currentUser;
            if (restrictedPage && !loggedIn) {
                $location.path('/search');
            }
        });
    }]);
