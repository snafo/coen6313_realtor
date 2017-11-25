'use strict';

angular.module('app.services')
    .factory('QueryServices', function (Restangular, $http, $q) {

        function stripRestangular(filters) {
            return Restangular.stripRestangular(filters);
        }

        function getProperty(param) {
            return Restangular.one("/property/get")
                .get({
                    'source': param.source,
                    'area': param.area,
                    'price': param.domain,
                    'year': param.startDate,
                    'type': param.endDate,
                    'limit': param.limit
                })
                .then(stripRestangular);
        }

        function createUser(param){
            return Restangular.one("/user/create")
                .post('',param)
                .then(stripRestangular);
        }

        return {
            getProperty : getProperty,
            createUser : createUser
        };
    });