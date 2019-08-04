package xyz.xiezc.mzi.controller;

import com.blade.ioc.annotation.Inject;
import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import xyz.xiezc.mzi.dao.AlbumMapper;
import xyz.xiezc.mzi.entity.Album;

@Path(restful = true)
public class WebController {


    @Inject
    AlbumMapper albumMapper;

    @GetRoute("/")
    public String index() {

        Album album = albumMapper.selectByPrimaryKey(3498);
        return album.getTitle();
    }


    @PostRoute("/save")
    public void saveUser(@Param String username) {
        System.out.println("username:" + username);
    }


}
