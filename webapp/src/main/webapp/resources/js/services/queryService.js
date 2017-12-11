'use strict';

angular.module('app.services')
    .factory('QueryServices', function (Restangular, $http, $q) {

        function stripRestangular(filters) {
            return Restangular.stripRestangular(filters);
        }

        function getProperty(param, name) {
            return Restangular.one("/property/get/" + name)
                .get({
                    'region' : param.region,
                    'minPrice' : param.minPrice,
                    'maxPrice' : param.maxPrice,
                    'source': param.source,
                    'area': param.area,
                    'bedroom': param.bedroom,
                    'bathroom': param.bathroom,
                    'year': param.year,
                    'type': param.type,
                    'limit': param.limit,
                    'keywords':param.keywords
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

        function getRecommend(uid){
            return Restangular.one("/favorite/recommend/" + uid)
                .get()
                .then(stripRestangular);
        }

        function getFavoriteProperty(uid){
            return Restangular.one("/favorite/property/" + uid)
                .get()
                .then(stripRestangular);
        }

        function getSearchParams(){
            return Restangular.one('/property/params')
                .get()
                .then(stripRestangular);
        }

        function getPrices(){
            return Restangular.one("/heatmap/price")
                .get()
                .then(stripRestangular);
        }

        function estimatePrice(param){
            return Restangular.one("/estimate/get")
                .get(param)
                .then(stripRestangular);
        }

        function getAgentList(uid){
            return Restangular.one("/favorite/agent/" + uid)
                .get()
                .then(stripRestangular);
        }

        function getFirmList(uid){
            return Restangular.one("/favorite/firm/" + uid)
                .get()
                .then(stripRestangular);
        }

        function getAgentProperty(name){
            return Restangular.one("/broker/agentproperty/")
                .get({
                    'agentName' : name
                })
                .then(stripRestangular);
        }

        function getFirmProperty(name){
            return Restangular.one("/broker/firmproperty/")
                .get({
                    'firmName' : name
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
            getFavoriteByUidCustom : getFavoriteByUidCustom,
            getPrices : getPrices,
            estimatePrice : estimatePrice,
            getFavoriteProperty : getFavoriteProperty,
            getRecommend : getRecommend,
            getSearchParams : getSearchParams,
            getAgentList : getAgentList,
            getFirmList : getFirmList,
            getAgentProperty : getAgentProperty,
            getFirmProperty : getFirmProperty
        };
    });