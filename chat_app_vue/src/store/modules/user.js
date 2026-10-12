

const state = {
    list: []
}

const mutations = {
    addlist(state, message) {
        return state.list = message;
    }
}

const actions = {

}

const getters = {

}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
}