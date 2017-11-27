"use strict";

angular
  .module('app.routing', ["app.services"])
  .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/search');

    $stateProvider
      .state('search',{
        url:'/search',
        templateUrl : 'resources/views/search.view.html',
        controller : 'SearchController'
      })
      .state('login',{
        url:'/login',
        templateUrl : 'resources/views/login.view.html',
        controller : 'LoginController',
        controllerAs: 'vm'
      })
      .state('register',{
        url:'/register',
        templateUrl : 'resources/views/register.view.html',
        controller : 'RegisterController'
      })
      .state('main', {
        url: '/main',
        templateUrl: 'resources/views/main.html',
        controller : 'cribsController',
        controllerAs: 'vm'
      })
      .state('user',{
        url:'/user',
        templateUrl : 'resources/views/user.html',
        controller : 'UserController'
      });
  }]);