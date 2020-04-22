import axios from "axios";

export const baseURL = 'http://192.168.43.78:8080';
export const request = axios.create({
  baseURL: baseURL, //Set your IPV4 here
  withCredentials: true,
})

request.interceptors.response.use(function (response) {
    return response
  }, function (error) {
    if(!error.response){
      console.log("No response from server")
      return
    }
    if (error.response.data.status === 401) {
      console.log('access denied, disconnecting user')
      //todo redirect the user to the login page with the router
    }
    return Promise.reject(error)
  })

  export default request