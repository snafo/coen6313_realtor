"use strict";

angular
  .module('app.routing', ["app.services"])
  .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/main');

    $stateProvider
      .state('main', {
        url: '/main',
        templateUrl: 'resources/views/main.html',
        controller : 'cribsController'
      })

      .state('user',{
        url:'/user',
        templateUrl : 'resources/views/user.html',
        controller : 'UserController'
      });
  }]);