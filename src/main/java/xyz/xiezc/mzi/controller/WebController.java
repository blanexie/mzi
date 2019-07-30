package xyz.xiezc.mzi.controller;

import com.blade.mvc.annotation.*;

@Path(restful = true)
public class WebController {


    @GetRoute("/")
    public String index(){
        return "{code:200}";
    }


    @PostRoute("/save")
    public void saveUser(@Param String username){
        System.out.println("username:" + username);
    }


}
