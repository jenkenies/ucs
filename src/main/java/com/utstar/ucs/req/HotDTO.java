package com.utstar.ucs.req;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class HotDTO {
  private String type;
  private String mc; 
  private String currentnum;
  @JsonIgnore
  private String createtime;
  @JsonIgnore
  private String st;
  
}
