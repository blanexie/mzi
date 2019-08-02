package xyz.xiezc.mzi.entity;

import lombok.Data;
import xyz.xiezc.mzi.common.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * t_album
 * @author 
 */
@Table("t_album")
@Data
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private String publishTime;

    private String type;

    private LocalDateTime createTime;

    /**
     * 封面id
     */
    private Integer coverId;

    private Integer see;



}