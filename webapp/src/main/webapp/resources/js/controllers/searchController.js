
angular
    .module('app.controllers')
    .controller('SearchController', function ($scope, $rootScope, $location, SearchResults, QueryServices, $cacheFactory) {
        $scope.param = {};
        var name = $rootScope.globals.currentUser.name;
        // var cache = $cacheFactory('realtor');

        $scope.search = function(){
            var param = getParam($scope.param);

            QueryServices.getProperty(param).then(function (response){
                if (response.code === 1){
                    // if (cache.get(name) !== null){
                    //     cache.remove(name);
                    // }
                    // cache.put(name, response.payLoad);
                    SearchResults.cleanProperty();
                    SearchResults.setProperty(response.payLoad);
                    $location.path('/searchResult');
                }
            });
        };

        function getParam(inputParam){
            var param = {};
            if (inputParam.minPrice){
                param.minPrice = '{gte:' + inputParam.minPrice + '}';
            }
            if (inputParam.maxPrice){
                param.maxPrice = inputParam.maxPrice;
            }
            if (inputParam.type){
                param.type = inputParam.type;
            }
            if (inputParam.bedroom){
                param.bedroom = '{gte:' + inputParam.bedroom + '}';
            }
            if (inputParam.bathroom){
                param.bathroom = '{gte:' +  inputParam.bathroom + '}';
            }
            if (inputParam.year){
                param.year = '{gte:' + inputParam.year + '}';

            }if (inputParam.region){
                param.region = inputParam.region;
            }

            if (inputParam.keywords){
                param.keywords = inputParam.keywords;
            }
            return param;
        }
    });
