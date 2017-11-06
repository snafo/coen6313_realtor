angular.module('app')
    .factory('cribsFactory', function($http){

        function getCribs() {
            return $http.get('resources/data/data.json');
        }

        return {
            getCribs: getCribs
        };
    });