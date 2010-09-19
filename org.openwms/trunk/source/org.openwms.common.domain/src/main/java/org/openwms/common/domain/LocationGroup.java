/*
 * openwms.org, the Open Warehouse Management System.
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software. If not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.openwms.common.domain.values.LocationGroupState;

/**
 * A LocationGroup - Used to group {@link Location}s logically together.
 * <p>
 * Used to group {@link Location}s with same characteristics.
 * </p>
 * 
 * @author <a href="mailto:openwms@googlemail.com">Heiko Scherrer</a>
 * @version $Revision$
 * @since 0.1
 * @see org.openwms.common.domain.Location
 */
@Entity
@Table(name = "COR_LOCATION_GROUP")
@NamedQueries( { @NamedQuery(name = "LocationGroup.findAll", query = "select lg from LocationGroup lg"),
        @NamedQuery(name = "LocationGroup.findByName", query = "select lg from LocationGroup lg where lg.name = ?1") })
public class LocationGroup extends AbstractEntity implements DomainObject, Serializable {

    /**
     * The serialVersionUID
     */
    private static final long serialVersionUID = -885742169116552293L;

    /**
     * Unique technical key.
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Long id;

    /**
     * Unique identifier of a LocationGroup.
     */
    @Column(name = "NAME", unique = true)
    private String name;

    /**
     * Description for this LocationGroup.
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * Type of this LocationGroup.
     */
    @Column(name = "GROUP_TYPE")
    private String groupType;

    /**
     * Is this LocationGroup be included in the calculation of
     * {@link org.openwms.common.domain.TransportUnit}s?
     * <p>
     * <code>true</code> : Location is been included in calculation of
     * {@link org.openwms.common.domain.TransportUnit}s (Default).<br>
     * <code>false</code>: Location is not been included in calculation of
     * {@link org.openwms.common.domain.TransportUnit}s.
     * </p>
     */
    @Column(name = "LOCATION_GROUP_COUNTING_ACTIVE")
    private boolean locationGroupCountingActive = true;

    /**
     * Number of {@link Location}s belonging to this LocationGroup.
     */
    @Column(name = "NO_LOCATIONS")
    private int noLocations = 0;

    /**
     * Infeed state of this LocationGroup.
     */
    @Column(name = "GROUP_STATE_IN")
    private LocationGroupState groupStateIn = LocationGroupState.AVAILABLE;

    /**
     * Outfeed state of this LocationGroup.
     */
    @Column(name = "GROUP_STATE_OUT")
    private LocationGroupState groupStateOut = LocationGroupState.AVAILABLE;

    /**
     * Maximum fill level of this LocationGroup.
     */
    @Column(name = "MAX_FILL_LEVEL")
    private float maxFillLevel = 0;

    /**
     * Date last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED")
    private Date lastUpdated;

    /**
     * Name of the PLC system, coupled to this LocationGroup.
     */
    @Column(name = "SYSTEM_CODE")
    private String systemCode;

    /**
     * Version field.
     */
    @Version
    @Column(name = "C_VERSION")
    private long version;

    /* ------------------- collection mapping ------------------- */
    /**
     * Parent LocationGroup.
     */
    @ManyToOne
    @JoinColumn(name = "PARENT")
    private LocationGroup parent;

    /**
     * Child LocationGroups.
     */
    @OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL })
    private Set<LocationGroup> locationGroups = new HashSet<LocationGroup>();

    /**
     * Child {@link Location}s.
     */
    @OneToMany(mappedBy = "locationGroup")
    private Set<Location> locations = new HashSet<Location>();

    /* ----------------------------- methods ------------------- */
    /**
     * Accessed by persistence provider.
     */
    @SuppressWarnings("unused")
    private LocationGroup() {}

    /**
     * Create a new LocationGroup with an unique name.
     * 
     * @param name
     *            The name of the LocationGroup
     */
    public LocationGroup(String name) {
        this.name = name;
    }

    /**
     * Return the technical key.
     * 
     * @return The technical unique key
     */
    public Long getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * Get the name of this LocationGroup.
     * 
     * @return The name of the LocationGroup
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * 
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the infeed state of this LocationGroup.
     * 
     * @return The state of infeed
     */
    public LocationGroupState getGroupStateIn() {
        return this.groupStateIn;
    }

    /**
     * Check whether infeed is allowed for this LocationGroup.
     * 
     * @return <code>true</code> if so.
     */
    public boolean isInfeedAllowed() {
        return (getGroupStateIn() == LocationGroupState.AVAILABLE);
    }

    /**
     * Check whether infeed is blocked for this LocationGroup.
     * 
     * @return <code>true</code> if so.
     */
    public boolean isInfeedBlocked() {
        return !isInfeedAllowed();
    }

    /**
     * Change the infeed state of this LocationGroup.
     * 
     * @param groupStateIn
     *            The state to set
     */
    public void setGroupStateIn(LocationGroupState groupStateIn) {
        this.groupStateIn = groupStateIn;
    }

    /**
     * Get the outfeed state of this LocationGroup.
     * 
     * @return The state of outfeed
     */
    public LocationGroupState getGroupStateOut() {
        return groupStateOut;
    }

    /**
     * Set the outfeed state of this LocationGroup.
     * 
     * @param groupStateOut
     *            The state to set
     */
    public void setGroupStateOut(LocationGroupState groupStateOut) {
        this.groupStateOut = groupStateOut;
    }

    /**
     * Returns the number of all sub {@link Location}s.
     * 
     * @return The number of {@link Location}s belonging to this group
     */
    public int getNoLocations() {
        return this.noLocations;
    }

    /**
     * Returns the maximum fill level of this LocationGroup.<br>
     * The maximum fill level defines how many {@link Location}s of this
     * LocationGroup can be occupied by
     * {@link org.openwms.common.domain.TransportUnit}s.
     * <p>
     * The maximum fill level is a value between 0 and 1 and reflects a
     * percentage value.
     * </p>
     * 
     * @return The maximum fill level
     */
    public float getMaxFillLevel() {
        return this.maxFillLevel;
    }

    /**
     * Set the maximum fill level for this LocationGroup.
     * <p>
     * Pass a value between 0 and 1.<br>
     * For example maxFillLevel = 0.85 means 85% of all sub {@link Location}s
     * can be occupied.
     * </p>
     * 
     * @param maxFillLevel
     *            The maximum fill level
     */
    public void setMaxFillLevel(float maxFillLevel) {
        this.maxFillLevel = maxFillLevel;
    }

    /**
     * Returns the type of this LocationGroup.
     * 
     * @return The type where this LocationGroup belongs to
     */
    public String getGroupType() {
        return this.groupType;
    }

    /**
     * Set the type of this LocationGroup.
     * 
     * @param groupType
     *            The type where this LocationGroup belongs to
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * Returns the date of last modification of this LocationGroup.
     * 
     * @return lastUpdated.
     */
    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Set the date of last modification for this LocationGroup.
     * 
     * @param lastUpdated
     *            The date to set
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Get the description text.
     * 
     * @return The Description as String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description text.
     * 
     * @param description
     *            The String to set as description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get parent LocationGroup.
     * 
     * @return The parent LocationGroup
     */
    public LocationGroup getParent() {
        return this.parent;
    }

    /**
     * Set parent LocationGroup.
     * 
     * @param parent
     *            The LocationGroup to set as parent
     */
    public void setParent(LocationGroup parent) {
        this.parent = parent;
    }

    /**
     * Get all child LocationGroups.
     * 
     * @return A Set of all LocationGroups having this one as parent
     */
    public Set<LocationGroup> getLocationGroups() {
        return locationGroups;
    }

    /**
     * Add a LocationGroup as child.
     * 
     * @param locationGroup
     *            The LocationGroup to be added as a child
     * @return <code>true</code> if the LocationGroup was new in the
     *         collection of LocationGroups, otherwise <code>false</code>
     */
    public boolean addLocationGroup(LocationGroup locationGroup) {
        if (locationGroup == null) {
            throw new IllegalArgumentException("LocationGroup to be added is null");
        }
        if (locationGroup.getParent() != null) {
            locationGroup.getParent().removeLocationGroup(locationGroup);
        }
        locationGroup.setParent(this);
        return locationGroups.add(locationGroup);
    }

    /**
     * Remove a LocationGroup from the list of children.
     * 
     * @param locationGroup
     *            The LocationGroup to be removed from the list of children
     * @return <code>true</code> if the LocationGroup was found and removed,
     *         otherwise <code>false</code>
     */
    public boolean removeLocationGroup(LocationGroup locationGroup) {
        if (locationGroup == null) {
            throw new IllegalArgumentException("LocationGroup to remove is null!");
        }
        locationGroup.setParent(null);
        return locationGroups.remove(locationGroup);
    }

    /**
     * Get all locations.
     * <p>
     * <strong><i>Note:</i> The returned collection is unmodifiable</strong>
     * </p>
     * 
     * @return A unmodifiable Set of all {@link Location}s that belong to this
     *         LocationGroup
     */
    public Set<Location> getLocations() {
        return Collections.unmodifiableSet(locations);
    }

    /**
     * Add a {@link Location} as child.
     * 
     * @param location
     *            The {@link Location} to be added as child
     * @return <code>true</code> if the {@link Location} was new in the
     *         collection of {@link Location}s, otherwise <code>false</code>
     */
    public boolean addLocation(Location location) {

        if (location == null) {
            throw new IllegalArgumentException("Location to be added is null");
        }
        if (location.getLocationGroup() != null) {
            location.getLocationGroup().removeLocation(location);
        }
        location.setLocationGroup(this);
        return locations.add(location);
    }

    /**
     * Remove a {@link Location} from the list of children.
     * 
     * @param location
     *            The {@link Location} to be removed from the list of children
     * @return <code>true</code> if the {@link Location} was found and
     *         removed, otherwise <code>false</code>
     */
    public boolean removeLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Child location is null!");
        }
        location.setLocationGroup(null);
        return locations.remove(location);
    }

    /**
     * Get the systemCode.
     * 
     * @return The systemCode
     */
    public String getSystemCode() {
        return systemCode;
    }

    /**
     * Set the systemCode.
     * 
     * @param systemCode
     *            The systemCode to set
     */
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    /**
     * Get the locationGroupCountingActive.
     * 
     * @return The locationGroupCountingActive
     */
    public boolean isLocationGroupCountingActive() {
        return locationGroupCountingActive;
    }

    /**
     * Set the locationGroupCountingActive.
     * 
     * @param locationGroupCountingActive
     *            The locationGroupCountingActive to set
     */
    public void setLocationGroupCountingActive(boolean locationGroupCountingActive) {
        this.locationGroupCountingActive = locationGroupCountingActive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getVersion() {
        return this.version;
    }

    /**
     * Return the name of the LocationGroup as String.
     * 
     * @see java.lang.Object#toString()
     * @return String
     */
    @Override
    public String toString() {
        return getName();
    }

}
