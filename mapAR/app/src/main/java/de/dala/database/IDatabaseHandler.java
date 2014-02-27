package de.dala.database;

import java.util.List;

import de.dala.common.EvaluationPicture;
import de.dala.common.MapItObject;
import de.dala.common.MapItObjectExtension;

/**
 * Interface, which contains every method used for database handling
 * 
 * @author Daniel Langerenken
 * 
 */
public interface IDatabaseHandler {

	/**
	 * Insert a new mapit object into the database
	 * 
	 * @param mapItObject
	 *            - new Object which should be inserted
	 * @return id of the object
	 */
	long addMapItObject(MapItObject mapItObject);

	/**
	 * Removes a specified mapit object from the database
	 * 
	 * @param mapItObject
	 *            - removed object
	 * @return whether the operation failed or succeeded
	 */
	boolean removeMapItObject(MapItObject mapItObject);

	/**
	 * Removes a specified mapit object from the database
	 * 
	 * @param id
	 *            of the mapItObject - removed object
	 * @return whether the operation failed or succeeded
	 */
	boolean removeMapItObjectById(long id);

	/**
	 * Removes every object from the database
	 * 
	 * @return whether the operation failed or succeeded
	 */
	boolean removeAllMapItObjects();

	/**
	 * Returns a mapit object from database
	 * 
	 * @param id
	 *            of object which should be returned
	 * @return object with the correspending id or null, if the object could not
	 *         be found
	 */
	MapItObject getMapItObjectById(long id);

	/**
	 * Returns every object from database
	 * 
	 * @return List of all mapit objects
	 */
	List<MapItObjectExtension> getAllMapItObjects();

	long addPicture(EvaluationPicture picture);
	
	List<EvaluationPicture> getAllPictures();
}
