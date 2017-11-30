"use strict";

angular
    .module('app.routing', ["app.services"])
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/login');

        $stateProvider
            .state('app', {
                abstract: true,
                templateUrl: 'resources/views/full.html'
            })
            .state('app.searchResult',{
                url:'/searchResult',
                templateUrl : 'resources/views/searchResult.html',
                controller : 'SearchResultController'
            })
            .state('app.favorite',{
                url:'/favorite',
                templateUrl : 'resources/views/searchResult.html',
                controller : 'FavoriteController'
            })
            .state('app.recommend',{
                url:'/recommend',
                templateUrl : 'resources/views/searchResult.html',
                controller : 'RecommendController'
            })
            .state('app.agent',{
                url:'/agent',
                templateUrl : 'resources/views/agent.html',
                controller : 'AgentController'
            })
            .state('appSimple',{
                abstract: true,
                templateUrl: 'resources/views/simple.html'
            })
            .state('appSimple.search',{
                url:'/search',
                templateUrl : 'resources/views/search.view.html',
                controller : 'SearchController'
            })
            .state('appSimple.login',{
                url:'/login',
                templateUrl : 'resources/views/login.view.html',
                controller : 'LoginController',
                controllerAs: 'vm'
            })
            .state('appSimple.register',{
                url:'/register',
                templateUrl : 'resources/views/register.view.html',
                controller : 'RegisterController'
            })
            .state('heatmap', {
                url: '/heatmap',
                templateUrl: 'resources/views/heatmap.view.html',
                controller : 'heatmapController'
            })
            .state('estimateprice', {
                url: '/estimateprice',
                templateUrl: 'resources/views/estimateprice.view.html',
                controller : 'estimationController',
                controllerAs: 'vm'
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