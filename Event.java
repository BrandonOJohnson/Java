
public class Event implements Comparable
{
  private int type;
  private double time;
    
  
 public Event (int type, double time)
 {
   this.type = type;
   this.time = time;
   
 }
 
 public int getType()
 {
  return this.type; 
 }
 
 public double getTime()
 {
  return this.time; 
 }
 
 public void setType(int type)
 {
  this.type = type; 
 }
 
 public void setTime(double time)
 {
  this.time = time; 
 }
 
 public int compareTo(Object _cmpEvent)
 {
   double _cmp_time = ((Event) _cmpEvent).getTime();
  if(this.time <  _cmp_time) return -1;
  if(this.time == _cmp_time) return 0;
  return 1;
 }
 
}