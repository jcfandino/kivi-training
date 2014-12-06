package io.github.jcfandino;

import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@Service
public class MainResource {
    @GET
    public View main() {
        return new MainView();
    }

}
