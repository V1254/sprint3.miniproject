package app

import app.domain.Organizer
import app.domain.Todo
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import spock.lang.*
import app.controller.*


@ContextConfiguration
@WebMvcTest(controllers=[DisplayTodoController.class,OrganizerController.class])
class OrganizerSpec extends Specification{

    @Autowired
    private WebApplicationContext wac

    private MockMvc mockMvc
    private ResultActions result


    def setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build()
    }

    // checkpoint
    def '0: decreasing priority throws exception'(){
        given: 'a todo with priority 10'
        Todo todo = new Todo("0","0")
        todo.setPriority(10)
        when: 'I set the priority of the todo to 9'
        todo.setPriority(9)
        then: ' a NullPointerException is thrown'
        thrown RuntimeException.class
    }

    def '1: performing a get/ redirects to /list'(){
        given: 'controlelr is setup'
        when: 'I perform an HTTP GET /'
        result = mockMvc.perform(get('/'))
        then: 'the status of the HTTP response should be 302'
        result.andExpect(status().is(302))
        and: 'I should be redirected to URL /list'
        result.andExpect(redirectedUrl('/list'))
    }

    def '2: performing a get /create , produces CreateTodo view with a todo with null description'(){
        given: 'controller is setup'
        and:   'the organizer has no todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        when:  'I perform an HTTP GET /create'
        result = mockMvc.perform(get('/create'))
        then:  'the status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and:   'I should see the view CreateTodo'
        result.andExpect(view().name('CreateTodo'))
        and:   'he model attribute todo has property description with value null'
        result.andExpect(model().attribute('description',is(nullValue())))
    }


    def '3: perfoming a get /list on an empty list results in the view NoTodo'(){
        given: 'the context of the controller is setup'
        and:   'the organizer has no todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        when:  ' I perform an HTTP GET /list'
        result = mockMvc.perform(get('/list'))
        then:  'the status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and:   'I should see the view NoTodo'
        result.andExpect(view().name('NoTodo'))
    }

    def '4: performing a /next on an organizer with todos creates the view NextTodo '(){
        given: 'the context of the controller is setup'
        and:   'the organizer has todos'
        Todo newTodo = new Todo('1','1')
        newTodo.setId(10)
        OrganizerApp.organizer.todos = new ArrayList<>()
        OrganizerApp.organizer.todos.add(newTodo)
        when:  'I perform an HTTP GET /next'
        result = mockMvc.perform(get('/next'))
        then:  'the status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and:   'I should see the view NextTodo'
        result.andExpect(view().name('NextTodo'))
    }

    def '5: performing a post /create with cancel params redirects to /list'(){
        given: 'the context of the controller is setup'
        when: 'I perform an HTTP POST /create with params'
        result = mockMvc.perform(post('/create').param('task','my Task')
                                                           .param('description','my Description')
                                                           .param('priority','0')
                                                           .param('cancel',''))
        then: 'the status of the HTTP response should be 302'
        result.andExpect(status().is(302))
        and:  'I should be redirected to URL /list'
        result.andExpect(redirectedUrl('/list'))
    }

    def '6: performing a post /create with add param results in the view CreateTodo with todo of priority 4'(){
        given: 'the context of the controller is setup'
        when: 'I perform an HTTP POST /create with params'
        result =  mockMvc.perform(post('/create').param('task','')
                                                            .param('description','my Description')
                                                            .param('priority','4')
                                                            .param('important','1')
                                                            .param('add',''))
        then: 'the status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and: 'I should see the view CreateTodo'
        result.andExpect(view().name('CreateTodo'))
        and: 'the model attribute todo has property priority equal to 4'
        result.andExpect(model().attribute('todo',hasProperty('priority',equalTo(4))))

    }
    // Part B:

    def '7: Whenever the organizer has no todos, the HTTP GET request /next should show the view NoTodo' (){
        given: 'the context of the controller is setup'
        and: 'the organizer has no todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        when: ' I perfom HTTP GET request /next'
        result = mockMvc.perform(get('/next'))
        then: 'i should see the view NoTodo'
        result.andExpect(view().name('NoTodo'))
    }

    def '8: Whenever the organizer has todos, the HTTP GET request /create should show the view CreateTodo' (){
        given: 'the context of the controller is setup'
        and: 'the organizer has todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        OrganizerApp.organizer.todos.add(new Todo('1','1'))
        when: ' the HTTP GET request /create is made'
        result = mockMvc.perform(get('/create'))
        then: 'the view CreateTodo is shown'
        result.andExpect(view().name('CreateTodo'))
    }

    def '9:The HTTP POST request /create with values should redirect to URL /list '(){
        given: 'the context of the controller is setup'
        when: 'i perform HTTP POST request /create with values'
        result = mockMvc.perform(post('/create')
                        .param('task','')
                        .param('description','my Description')
                        .param('priority','0')
                        .param('cancel',''))
        then: 'i should be redirected to /list'
        result.andExpect(redirectedUrl('/list'))
    }

    def '10:The HTTP POST request /create should show the view CreateTodo and the model attribute todo should have errors '(){
        given: 'the context of the controller is setup'
        when: 'i perform HTTP POST request /create with values'
        result = mockMvc.perform(post('/create')
                .param('task','')
                .param('description','')
                .param('priority','0')
                .param('important','1')
                .param('add',''))
        then: 'the view CreateTodo is displayed'
        result.andExpect(view().name('CreateTodo'))
        and: 'the model attribute todo should have errors'
        result.andExpect(model().attributeHasErrors('todo'))
    }




}
