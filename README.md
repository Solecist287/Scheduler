# Scheduler
Desktop application where the user can schedule events and view them in timetables.
## Installation
Clone this repository and import it into Eclipse.
```
https://github.com/Solecist287/Scheduler.git
```
## Tools Used
* JavaFX
* Eclipse
## Features And How To Use Them
* Add events
  * Click "+ Event" button, fill out the form, and save it
* Remove events
  * Can click existing event to view it, then remove it
  * Can click existing event to view it, then modify it, and remove it 
* Modify events
  * Click to view existing event and click to modify it
* Events are saved through serialization
  * This happens automatically upon shutdown
* Click to view event info
  * Click existing event
* View events in day, week, month, and year timetables
  * Choose "day", "week", "month", or "year" from the dropdown box (month and year not yet implemented)
* Traverse a calendar or other shortcut buttons to view events
  * For the calendar, use the arrows and boxes to find the desired date
  * Can click today button to direct user to events on that current day
  * Can click left or right arrows next to the today button to go to the previous or next day/week/month/year depending on the dropdown box's current value
