
angular.module('app.controllers').
controller('AgentController', function ($scope, $rootScope, QueryServices, NgMap) {
    $rootScope.currentTab = 'agent';

    $scope.cribs;

    $scope.saveDisabled =false;

    $scope.user = {};

    NgMap.getMap().then(function (map) {
        $scope.map = map;
    });

    $scope.agents = [];

    $scope.firms = [];

    $scope.selections = {
        agent : 'Agent',
        firm : 'Firm'
    };

    $scope.selected = 'agent';

    $scope.selectedFirm;

    $scope.selectedAgent;

    $scope.showProp = function(event, prop) {
        $scope.selectedProp = prop;
        $scope.map.showInfoWindow('myInfoWindow',prop.propertyId.toString());
    };

    initController();
    function initController() {
        QueryServices.getUserByNameCustom($rootScope.globals.currentUser.name)
            .then(function (result) {
                if (result.payLoad[0].name === $rootScope.globals.currentUser.name) {
                    $scope.user = result.payLoad[0];
                    QueryServices.getAgentList(result.payLoad[0].id)
                        .then(function (result){
                            if (result.code === 1 ) {
                                result.payLoad.forEach(function(data){
                                    if (data._id){
                                        $scope.agents.push(
                                            {
                                                _id : data._id[0],
                                                total : data.total
                                            });
                                    }
                                });
                                // $scope.cribs = result.payLoad;
                            } else {
                                alert(result.message);
                            }
                        });
                    QueryServices.getFirmList(result.payLoad[0].id)
                        .then(function (result){
                            if (result.code === 1 ) {
                                result.payLoad.forEach(function(data){
                                    if (data._id){
                                        $scope.firms.push(data);
                                    }
                                });
                                // $scope.cribs = result.payLoad;
                            } else {
                                alert(result.message);
                            }
                        });

                } else {
                    // vm.user = $rootScope.globals.currentUser;
                    // console.log("cannot map");
                }
            });
    }

    $scope.$watch('selectedAgent', function(newValue, oldValue){
        if (newValue !== oldValue){
            QueryServices.getAgentProperty(newValue).then(function(result){
                if (result.code === 1 ) {
                    $scope.cribs = result.payLoad;
                } else {
                    alert("Failed to load property list");
                }
            });
        }
    },true);

    $scope.$watch('selectedFirm', function(newValue, oldValue){
        if (newValue !== oldValue){
            QueryServices.getFirmProperty(newValue).then(function(result){
                if (result.code === 1 ) {
                    $scope.cribs = result.payLoad;
                } else {
                    alert("Failed to load property list");
                }
            });
        }
    },true);

    $scope.createFavorite = function (crib) {
        var param = {uid: $scope.user.id ,propertyid: crib.propertyId};
        console.log(param);
        QueryServices.createFavorite(param).then(function (result){
            if(result.code === 1){
                var index = $scope.cribs.indexOf(crib);
                $scope.cribs[index].favorite = true;
            } else{
                alert("Failed to save to favorite list");
            }
        });
    };
});

