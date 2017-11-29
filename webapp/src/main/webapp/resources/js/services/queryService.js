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

        function getUserByName(name){
            return Restangular.one("/user/findbyname/")
                .get(name)
                .then(stripRestangular);
        }

        function getUserByNameCustom(name){
            return Restangular.one("/user/findbynamecustom")
                .get({
                    'name': name
                })
                .then(stripRestangular);
        }

        function createUser(param){
            return Restangular.one("/user/create")
                .post('',param)
                .then(stripRestangular);
        }

        function createFavorite(param){
            return Restangular.one("/favorite/create")
                .post('',param)
                .then(stripRestangular);
        }

        function removeFavorite(param){
            return Restangular.one("/favorite/remove")
                .post('',param)
                .then(stripRestangular);
        }

        function getFavoriteByUidCustom(uid){
            return Restangular.one("/favorite/findbyuidcustom")
                .get({
                    'uid': uid
                })
                .then(stripRestangular);
        }

        return {
            getProperty : getProperty,
            createUser : createUser,
            getUserByName : getUserByName,
            getUserByNameCustom : getUserByNameCustom,
            createFavorite : createFavorite,
            removeFavorite : removeFavorite,
            getFavoriteByUidCustom : getFavoriteByUidCustom
        };
    });