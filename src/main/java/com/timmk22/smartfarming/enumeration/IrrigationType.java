package com.timmk22.smartfarming.enumeration;

/**
 * Describes the irrigation method used for a planting entry.
 */
public enum IrrigationType {

    /**
     * Water applied directly to the root zone below the surface.
     */
    DRIP,

    /**
     * Overhead spray systems simulating rainfall.
     */
    SPRINKLER,

    /**
     * Controlled flooding of flat fields.
     */
    FLOOD,

    /**
     * Water delivered along furrows between crop rows.
     */
    FURROW,

    /**
     * Rainfall only — no artificial irrigation.
     */
    RAIN_FED,

    /**
     * Manual watering by hand or handheld equipment.
     */
    MANUAL
}
