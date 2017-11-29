angular.module('app')
    .factory('favoriteFactory', function($http){

        function getFavorites() {
            return $http.get('resources/data/favorites.json');
        }

        return {
            getFavorites: getFavorites
        };
    });