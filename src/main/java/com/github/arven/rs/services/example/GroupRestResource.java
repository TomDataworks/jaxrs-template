/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.arven.rs.services.example;

import static com.github.arven.rs.services.example.MicroBlogRestResource.MAX_LIST_SPAN;
import com.github.arven.rs.types.DataList;
import java.io.Serializable;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * This is the Group representation for the RESTful web service. You can
 * get group info, get a list of members, leave a group, or join a group.
 *
 * @author Brian Becker
 */
public class GroupRestResource implements Serializable {
        
    private final MicroBlogService blogService;
    
    public GroupRestResource(MicroBlogService blogService) {
        this.blogService = blogService;
    }
    
    /**
     * For a given user, this method creates a group and inserts the user into
     * the group. Any user can call this method and any group name can be used
     * as long as the group name is available.
     * 
     * @param name
     * @param group
     * @param ctx 
     */
    @PUT @RolesAllowed({"User"})
    public void createGroup(@PathParam("group") String name, GroupData group, final @Context SecurityContext ctx) {
        group.setId(name);
        blogService.addGroup(group, ctx.getUserPrincipal().getName());
    }    
    
    /**
     * For a given group, this method gets the information and returns it back
     * to the user. Any group can be viewed by any user.
     * 
     * @param name
     * @return 
     */
    @GET
    public GroupData getGroupInfo(@PathParam("group") String name) {
        return blogService.getGroup(name);
    }
    
    /**
     * For the given group name, get the list of members. This can be called
     * by any user regardless of group membership.
     * 
     * @param name
     * @param offset
     * @return 
     */
    @Path("/members") @GET
    public DataList getGroupMembers(@PathParam("group") String name, @MatrixParam("offset") Integer offset) {
        return new DataList(blogService.getGroupMembers(name), offset, MAX_LIST_SPAN, false);
    }
    
    /**
     * For the given group name, join the group. This can be called by any user
     * which is not currently a member of the group, and they will then be
     * shown in the members list.
     * 
     * @param name
     * @param user
     * @param ctx 
     */
    @Path("/members/{user}") @PUT @RolesAllowed({"User"})
    public void joinGroup(@PathParam("group") String name, @PathParam("user") String user, final @Context SecurityContext ctx) {
        if(user.equals(ctx.getUserPrincipal().getName())) {
            blogService.addGroupMember(name, ctx.getUserPrincipal().getName());
        }
    }
    
    /**
     * For the authenticated user, this method leaves the chosen group. If the
     * group is empty upon leaving, the group will be disbanded as there are
     * no members, and it will be available for any other user to create.
     * 
     * @param name
     * @param user
     * @param ctx 
     */
    @Path("/members/{user}") @DELETE @RolesAllowed({"User"})
    public void leaveGroup(@PathParam("group") String name, @PathParam("user") String user, final @Context SecurityContext ctx) {
        if(user.equals(ctx.getUserPrincipal().getName())) {
            blogService.leaveGroup(name, ctx.getUserPrincipal().getName());
        }
    }
    
}
