/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import eu.gloria.gs.services.experiment.base.data.TimeSlot;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 *
 */
public class ReserveOnlineExperimentRequest extends ListAvailableTimeSlotsRequest {

	private TimeSlot timeSlot;

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}
}
