package com.github.arven.rs.services.example;

import com.github.arven.rs.types.DataList;
import com.github.arven.rs.types.ReferenceList;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * MicroBlogRestService is the REST service front end for the MicroBlogService.
 * Most of the calls are delegated directly to the MicroBlogService. There are
 * a number of annotations on each of the paths, which specify the majority of
 * the REST parameters.
 * 
 * @author Brian Becker
 */
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MicroBlogRestService {
    
    public static int MAX_LIST_SPAN = 10;
    
    @Inject
    private MicroBlogService blogService;

    @Path("/user") @POST 
    public void addUser(UserData user) {
        blogService.addUser(user);
    }
    
    @Path("/user/{name}") @GET
    public UserData getUser(@PathParam("name") String name) {
        return blogService.getUser(name);
    }
    
    @Path("/user/{name}/friends") @GET
    public ReferenceList getFriendsList(@PathParam("name") String name, @MatrixParam("offset") Integer offset) {
        return new ReferenceList(blogService.getFriends(name), offset, MAX_LIST_SPAN, false);
    }
    
    @Path("/user/{name}/friends/{friend}") @PUT @RolesAllowed({"ROLE_USER"})
    public void addFriend(@PathParam("name") String name, @PathParam("friend") String friend, final @Context SecurityContext ctx) {
        if(ctx.getUserPrincipal().getName().equals(name)) {
            blogService.addFriend(name, friend);
        }
    }
    
    @Path("/user/{name}/friends/{friend}") @DELETE @RolesAllowed({"ROLE_USER"})
    public void removeFriend(@PathParam("name") String name, @PathParam("friend") String friend, final @Context SecurityContext ctx) {
        if(ctx.getUserPrincipal().getName().equals(name)) {
            blogService.removeFriend(name, friend);
        }
    }
    
    @Path("/post") @POST @RolesAllowed({"ROLE_USER"})
    public void postMessage(MessageData post, final @Context SecurityContext ctx) {
        blogService.addPost(ctx.getUserPrincipal().getName(), post);
    }
    
    @Path("/post/{name}") @GET
    public DataList getMessagesByUser(@PathParam("name") String name, @MatrixParam("offset") Integer offset) {
        return new DataList(blogService.getPosts(name), offset, MAX_LIST_SPAN, true);
    }    
    
    @Path("/group") @POST @RolesAllowed({"ROLE_USER"})
    public void createGroup(GroupData group, final @Context SecurityContext ctx) {
        blogService.addGroup(group, ctx.getUserPrincipal().getName());
    }
    
    @Path("/group/{name}") @GET
    public GroupData getGroupInfo(@PathParam("name") String name) {
        return blogService.getGroup(name);
    }
    
    @Path("/group/{name}") @DELETE @RolesAllowed({"ROLE_USER"})
    public void leaveOrDisbandGroup(@PathParam("name") String name, final @Context SecurityContext ctx) {
        blogService.leaveGroup(name, ctx.getUserPrincipal().getName());
    }
    
    @Path("/group/{name}/members") @GET
    public ReferenceList getGroupMembers(@PathParam("name") String name, @MatrixParam("offset") Integer offset) {
        return new ReferenceList(blogService.getGroupMembers(name), offset, MAX_LIST_SPAN, false);
    }
    
    @Path("/group/{name}/join") @POST @RolesAllowed({"ROLE_USER"})
    public void joinGroup(@PathParam("name") String name, final @Context SecurityContext ctx) {
        blogService.addGroupMember(name, ctx.getUserPrincipal().getName());
    }
 
}