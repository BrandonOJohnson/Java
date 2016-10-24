import java.util.*;

public class EventList
{
  PriorityQueue<Event> events;
  
  public EventList()
  {
    events = new PriorityQueue<Event>();
  }
  
  public void enqueue(Event e)
  {
    events.offer(e);
  }
  
  public void dequeue()
  {
    events.poll();
  }
  
  public Event getMin()
  {
    return events.peek();
  }
}