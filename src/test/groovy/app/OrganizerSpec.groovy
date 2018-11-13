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
        given:'A todo with priority 10'
        Todo todo = new Todo("0","0")
        todo.setPriority(10)
        when: 'I set the priority of the todo to 9'
        todo.setPriority(9)
        then: 'A RuntimeException is thrown'
        thrown RuntimeException.class
    }

    def '1: performing a HTTP GET / redirects to /list'(){
        given:'The context of the controller is setup'
        when: 'I perform an HTTP GET /'
        result = mockMvc.perform(get('/'))
        then: 'The status of the HTTP response should be 302'
        result.andExpect(status().is(302))
        and:  'I should be redirected to URL /list'
        result.andExpect(redirectedUrl('/list'))
    }

    def '2: HTTP GET /create shows CreateTodo with a null description'(){
        given: 'The context of the controller is setup'
        and:   'The organizer has no todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        when:  'I perform a HTTP GET /create'
        result = mockMvc.perform(get('/create'))
        then:  'The status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and:   'I should see the view CreateTodo'
        result.andExpect(view().name('CreateTodo'))
        and:   'The model attribute todo has property description with value null'
        result.andExpect(model().attribute('description',is(nullValue())))
    }


    def '3: HTTP GET /list shows NoTodo view'(){
        given: 'The context of the controller is setup'
        and:   'The organizer has no todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        when:  'I perform an HTTP GET /list'
        result = mockMvc.perform(get('/list'))
        then:  'The status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and:   'I should see the view NoTodo'
        result.andExpect(view().name('NoTodo'))
    }

    def '4: HTTP GET /next on non empty organizer shows NextTodo view '(){
        given: 'The context of the controller is setup'
        and:   'The organizer has todos'
        Todo newTodo = new Todo('1','1')
        newTodo.setId(10)
        OrganizerApp.organizer.todos = new ArrayList<>()
        OrganizerApp.organizer.todos.add(newTodo)
        when:  'I perform an HTTP GET /next'
        result = mockMvc.perform(get('/next'))
        then:  'The status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and:   'I should see the view NextTodo'
        result.andExpect(view().name('NextTodo'))
    }

    def '5: HTTP POST /create with cancel param redirects to /list'(){
        given: 'The context of the controller is setup'
        when:  'I perform an HTTP POST /create with params'
        result = mockMvc.perform(post('/create').param('task','my Task')
                                                           .param('description','my Description')
                                                           .param('priority','0')
                                                           .param('cancel',''))
        then: 'The status of the HTTP response should be 302'
        result.andExpect(status().is(302))
        and:  'I should be redirected to /list'
        result.andExpect(redirectedUrl('/list'))
    }

    def '6: HTTP POST /create with params adds a todo with priority 4 to the model'(){
        given: 'The context of the controller is setup'
        when:  'I perform an HTTP POST /create with params'
        result =  mockMvc.perform(post('/create').param('task','')
                                                            .param('description','my Description')
                                                            .param('priority','4')
                                                            .param('important','1')
                                                            .param('add',''))
        then: 'The status of the HTTP response should be Ok'
        result.andExpect(status().isOk())
        and:  'I should see the view CreateTodo'
        result.andExpect(view().name('CreateTodo'))
        and:  'The model attribute todo has property priority equal to 4'
        result.andExpect(model().attribute('todo',hasProperty('priority',equalTo(4))))

    }
    // Part B:
    def '7: Whenever the organizer has no todos, the HTTP GET request /next should show the view NoTodo' (){
        given: 'The context of the controller is setup'
        and:   'The organizer has no todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        when:  'I perform HTTP GET request /next'
        result = mockMvc.perform(get('/next'))
        then:  'I should see the view NoTodo'
        result.andExpect(view().name('NoTodo'))
    }

    def '8: Whenever the organizer has todos, the HTTP GET request /create should show the view CreateTodo' (){
        given: 'The context of the controller is setup'
        and:   'The organizer has todos'
        OrganizerApp.organizer.todos = new ArrayList<>()
        OrganizerApp.organizer.addTodo(new Todo('1','1'))
        when:  'The HTTP GET request /create is made'
        result = mockMvc.perform(get('/create'))
        then:  'The view CreateTodo is shown'
        result.andExpect(view().name('CreateTodo'))
    }

    def '9:The HTTP POST request /create with values should redirect to URL /list '(){
        given: 'The context of the controller is setup'
        when:  'I perform HTTP POST request /create with values'
        result = mockMvc.perform(post('/create')
                        .param('task','')
                        .param('description','my Description')
                        .param('priority','0')
                        .param('cancel',''))
        then: 'I should be redirected to /list'
        result.andExpect(redirectedUrl('/list'))
    }

    def '10:The HTTP POST request /create should show the view CreateTodo and the model attribute todo should have errors '(){
        given: 'The context of the controller is setup'
        when:  'I perform HTTP POST request /create with values'
        result = mockMvc.perform(post('/create')
                .param('task','')
                .param('description','')
                .param('priority','0')
                .param('important','1')
                .param('add',''))
        then:  'The view CreateTodo is displayed'
        result.andExpect(view().name('CreateTodo'))
        and:   'The model attribute todo should have errors'
        result.andExpect(model().attributeHasErrors('todo'))
    }

    // Part C:

    def '11: valid todos are added to Organizer'(){
        given: 'The context of the controller is setup'
        and:   'An empty organizer'
        OrganizerApp.organizer.todos = new ArrayList<>()
        when:  'I perform a HTTP POST request /create with valid todo params'
        result = mockMvc.perform(post('/create')
                        .param('task','my Task')
                        .param('description','my Description')
                        .param('priority','10')
                        .param('important','1')
                        .param('add',''))
        then:  'The todo is added to the organizer'
        assertThat(OrganizerApp.organizer.todos.size(),equalTo(1))
        and:   'The todo has the priority of 10'
        assertThat(OrganizerApp.organizer.todos.get(0),hasProperty('priority',equalTo(10)))
        and:   'Im redirected to /list'
        result.andExpect(redirectedUrl('/list'))
    }

    def '12: can delete todos from Organizer'(){
        given: 'The context of the controller is setup'
        and:   'A todo with id=10 in organizer '
        OrganizerApp.organizer.todos = new ArrayList<>()
        Todo test = new Todo('test','test')
        test.setId(10)
        OrganizerApp.organizer.addTodo(test)
        when:  'I perform a HTTP GET /delete with id = 10'
        result = mockMvc.perform(get('/delete').param('id','10'))
        then:  'The todo with id 10 is deleted from the organizer'
        assertThat(OrganizerApp.organizer.todos.size(),equalTo(0))
        and:   'I am redirected to /list'
        result.andExpect(redirectedUrl('/list'))

    }

    def '13: organizer with a todo should be displayed correctly in ListTodos'(){
        given: 'The context of the controller is setup'
        and:   'A non-empty organizer'
        Todo t = new Todo('1','1')
        t.setId(10)
        OrganizerApp.organizer.todos = new ArrayList<>()
        OrganizerApp.organizer.addTodo(t)
        when:  'I perform a HTTP GET request /list'
        result = mockMvc.perform(get('/list'))
        then:  'The model has the attribute todos'
        result.andExpect(model().attributeExists('todos'))
        and :  'The view ListTodos is shown'
        result.andExpect(view().name('ListTodos'))
    }


    def '14: Given a non important todo with priority 110, it is rejected'(){
        given: 'The context of the controller is setup'
        when:  'I perform a HTTP POST /create  request with priority 110 and non important'
        result = mockMvc.perform(post('/create')
                        .param('task','my Task')
                        .param('description','my Description')
                        .param('priority','110')
                        .param('add',''))
        then:  'The view CreateTodo is displayed'
        result.andExpect(view().name('CreateTodo'))
        and:   'The model has priority errors'
        result.andExpect(model().attributeHasFieldErrors('todo','priority'))
    }

    def '15: a non important Todo Description cannot be longer than 20 characters'(){
        given: 'The context of the controller is setup'
        when:  'I perform a HTTP POST /create request with a description length greater than 20'
        String toAdd = '12345678910111213141516'
        result = mockMvc.perform(post('/create')
                .param('task','my Task')
                .param('description',toAdd)
                .param('priority','90')
                .param('add',''))
        then:  'The view CreateTodo is displayed'
        result.andExpect(view().name('CreateTodo'))
        and:   'The model has Description Errors'
        result.andExpect(model().attributeHasFieldErrors('todo','description'))
    }

    def '16: a non important Todo with a description length less than 20 is accepted'(){
        given: 'The context of the controller is setup'
        when:  'I perform a HTTP POST  /create request with a description length less than 20'
        result = mockMvc.perform(post('/create')
                        .param('task','my Task')
                        .param('description','my Description')
                        .param('priority','90')
                        .param('add',''))
        then:  'I am redirected to /list'
        result.andExpect(redirectedUrl('/list'))

    }

    def '17: Given an Important Todo, its task name cannot equal its description'(){
        given: 'The context of the controller is setup'
        when:  'I perform a HTTP POST /create request where taskName is the same as description'
        result = mockMvc.perform(post('/create')
                        .param('task','my Task')
                        .param('description','my Task')
                        .param('priority','90')
                        .param('important','1')
                        .param('add','')
        )
        then:  'The view CreateTodo is displayed'
        result.andExpect(view().name('CreateTodo'))
        and:   'The model has task errors'
        result.andExpect(model().attributeHasFieldErrors('todo','task'))
    }

}
