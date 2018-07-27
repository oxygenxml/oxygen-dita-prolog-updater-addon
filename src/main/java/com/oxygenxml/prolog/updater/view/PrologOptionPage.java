package com.oxygenxml.prolog.updater.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.oxygenxml.prolog.updater.prolog.content.DateFormats;
import com.oxygenxml.prolog.updater.tags.OptionKeys;
import com.oxygenxml.prolog.updater.tags.Tags;

import ro.sync.exml.workspace.api.PluginResourceBundle;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * The option page for the prolog updater plugin.
 */
public class PrologOptionPage extends JPanel {
	/**
	 * THe serial ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The text field that contains the author name;
	 */
	private JTextField authorTextField = new JTextField();

	/**
	 * CheckBox for enable prolog update in DITA topics
	 */
	private JCheckBox topicEnableUpdate;
	/**
	 * CheckBox which sets the creator in DITA topics.
	 */
	private JCheckBox topicSetCreator;
	/**
	 * CheckBox which sets the created date in DITA topics.
	 */
	private JCheckBox topicSetCreated;
	/**
	 * CheckBox which updates the contributor in DITA topics.
	 */
	private JCheckBox topicUpdateContributor;
	/**
	 * CheckBox which updates the revised date in DITA topics.
	 */
	private JCheckBox topicUpdateRevised;

	/**
	 * CheckBox for enable prolog update in DITA maps
	 */
	private JCheckBox mapEnableUpdate;
	/**
	 * CheckBox which sets the creator in DITA maps.
	 */
	private JCheckBox mapSetCreator;
	/**
	 * CheckBox which sets the created date in DITA maps.
	 */
	private JCheckBox mapSetCreated;
	/**
	 * CheckBox which updates the contributor in DITA maps.
	 */
	private JCheckBox mapUpdateContributor;
	/**
	 * CheckBox which updates the revised date in DITA maps.
	 */
	private JCheckBox mapUpdateRevised;

	/**
	 * Combo for select the date format.
	 */
	private JComboBox<String> dateFormatCombo;
		
	/**
	 * Default value for check boxes in boolean format
	 */
	private static final boolean CHECK_SELECTED_DEFAULT_BOOLEAN = true;

	/**
	 * Default value for check boxes in String format
	 */
	private static final String CHECK_SELECTED_DEFAULT = String.valueOf(CHECK_SELECTED_DEFAULT_BOOLEAN);

	/**
	 * Default name of the author.
	 */
	private static final String AUTHOR_DEFAULT = System.getProperty("user.name");

	/**
	 * Left indent for check boxes.
	 */
	private int leftIndent = 0;

	/**
	 * Constructor.
	 */
	public PrologOptionPage() {
		super(new GridBagLayout());

		StandalonePluginWorkspace pluginWorkspace = (StandalonePluginWorkspace) PluginWorkspaceProvider
				.getPluginWorkspace();
		PluginResourceBundle messages = pluginWorkspace.getResourceBundle();

		topicEnableUpdate = new JCheckBox(messages.getMessage(Tags.ENABLE_UPDATE_ON_SAVE));
		topicSetCreator = new JCheckBox(messages.getMessage(Tags.SET_CREATOR));
		topicSetCreated = new JCheckBox(messages.getMessage(Tags.SET_CREATED_DATE));
		topicUpdateContributor = new JCheckBox(messages.getMessage(Tags.UPDATE_CONTRIBUTOR));
		topicUpdateRevised = new JCheckBox(messages.getMessage(Tags.UPDATE_REVISED_DATES));

		mapEnableUpdate = new JCheckBox(messages.getMessage(Tags.ENABLE_UPDATE_ON_SAVE));
		mapSetCreator = new JCheckBox(messages.getMessage(Tags.SET_CREATOR));
		mapSetCreated = new JCheckBox(messages.getMessage(Tags.SET_CREATED_DATE));
		mapUpdateContributor = new JCheckBox(messages.getMessage(Tags.UPDATE_CONTRIBUTOR));
		mapUpdateRevised = new JCheckBox(messages.getMessage(Tags.UPDATE_REVISED_DATES));

		leftIndent = new JCheckBox().getPreferredSize().width;

		dateFormatCombo = new JComboBox<String>(DateFormats.DATE_PATTERNS);
		dateFormatCombo.setEditable(true);
		
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		constr.anchor = GridBagConstraints.WEST;
		// Author label
		add(new JLabel(messages.getMessage(Tags.AUTHOR) + ":"), constr);

		constr.gridx++;
		constr.weightx = 1;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets.left = leftIndent;
		// Author field
		add(authorTextField, constr);

		constr.gridx = 0;
		constr.gridy++;
		constr.weightx = 0;
		constr.insets.left = 0;
		constr.insets.top = 5;
		// Date format label
		add(new JLabel(messages.getMessage(Tags.DATE_FORMAT) + ":"), constr);

		constr.gridx++;
		constr.weightx = 1;
		constr.insets.left = leftIndent;
		// Date format combo.
		add(dateFormatCombo, constr);
		
		//
		// DITA topic
		//
		constr.gridx = 0;
		constr.gridy++;
		constr.gridwidth = 2;
		constr.insets.top = 0;
		constr.insets.left = 0;
		add(new SectionPane(messages.getMessage(Tags.DITA_TOPIC)), constr);

		constr.gridy++;
		add(topicEnableUpdate, constr);

		constr.gridy++;
		constr.insets.left = leftIndent;
		add(topicSetCreator, constr);

		constr.gridy++;
		add(topicSetCreated, constr);

		constr.gridy++;
		add(topicUpdateContributor, constr);

		constr.gridy++;
		add(topicUpdateRevised, constr);

		//
		// DITA map
		//
		constr.gridy++;
		constr.insets.left = 0;
		add(new SectionPane(messages.getMessage(Tags.DITA_MAP)), constr);

		constr.gridy++;
		add(mapEnableUpdate, constr);

		constr.gridy++;
		constr.insets.left = leftIndent;
		add(mapSetCreator, constr);

		constr.gridy++;
		add(mapSetCreated, constr);

		constr.gridy++;
		add(mapUpdateContributor, constr);

		constr.gridy++;
		add(mapUpdateRevised, constr);

		// Push everything to top
		constr.gridx = 0;
		constr.gridy++;
		constr.gridwidth = 2;
		constr.weighty = 1;
		constr.fill = GridBagConstraints.BOTH;
		constr.insets.left = 0;
		add(new JPanel(), constr);

		topicEnableUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleTopicChecks(topicEnableUpdate.isSelected());
			}
		});

		mapEnableUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleMapChecks(mapEnableUpdate.isSelected());
			}
		});

		// load the page state from WSOptionStorage.
		loadPageState();
	}

	/**
	 * Enables or disables all topic related check boxes.
	 * 
	 * @param enabled
	 *          <code>true</code> to enable all topic related check boxes.
	 */
	private void toggleTopicChecks(boolean enabled) {
		topicSetCreator.setEnabled(enabled);
		topicSetCreated.setEnabled(enabled);
		topicUpdateContributor.setEnabled(enabled);
		topicUpdateRevised.setEnabled(enabled);
	}

	/**
	 * Enables or disables all map related check boxes.
	 * 
	 * @param enabled
	 *          <code>true</code> if map should accept updates from plugin.
	 */
	private void toggleMapChecks(boolean enabled) {
		mapSetCreator.setEnabled(enabled);
		mapSetCreated.setEnabled(enabled);
		mapUpdateContributor.setEnabled(enabled);
		mapUpdateRevised.setEnabled(enabled);
	}

	/**
	 * Save the state of PrologOptionPage in WSOptionStorage.
	 */
	public void savePageState() {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		if (optionsStorage != null) {
			// Save the author name.
			optionsStorage.setOption(OptionKeys.AUTHOR_NAME, authorTextField.getText());
			// Save the date format.
			optionsStorage.setOption(OptionKeys.DATE_FORMAT, (String)dateFormatCombo.getSelectedItem());

			// Save the state of check boxes from DITA topic.
			optionsStorage.setOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, String.valueOf(topicEnableUpdate.isSelected()));
			optionsStorage.setOption(OptionKeys.TOPIC_SET_CREATOR, String.valueOf(topicSetCreator.isSelected()));
			optionsStorage.setOption(OptionKeys.TOPIC_SET_CREATED_DATE, String.valueOf(topicSetCreated.isSelected()));
			optionsStorage.setOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR,
					String.valueOf(topicUpdateContributor.isSelected()));
			optionsStorage.setOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, String.valueOf(topicUpdateRevised.isSelected()));

			// Save state of check boxes from DITA map.
			optionsStorage.setOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, String.valueOf(mapEnableUpdate.isSelected()));
			optionsStorage.setOption(OptionKeys.MAP_SET_CREATOR, String.valueOf(mapSetCreator.isSelected()));
			optionsStorage.setOption(OptionKeys.MAP_SET_CREATED_DATE, String.valueOf(mapSetCreated.isSelected()));
			optionsStorage.setOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, String.valueOf(mapUpdateContributor.isSelected()));
			optionsStorage.setOption(OptionKeys.MAP_UPDATE_REVISED_DATES, String.valueOf(mapUpdateRevised.isSelected()));
		}
	}

	/**
	 * Load the state of PrologOptionPage from WSOptionStorage.
	 */
	private void loadPageState() {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		String value = "";
		if (optionsStorage != null) {
			// Load the author name.
			value = optionsStorage.getOption(OptionKeys.AUTHOR_NAME, AUTHOR_DEFAULT);
			authorTextField.setText(value);
		
			// Load the date format name.
			value = optionsStorage.getOption(OptionKeys.DATE_FORMAT, DateFormats.DEFAULT_DATE_PATTERN);
			dateFormatCombo.getModel().setSelectedItem(value);

			// Load DITA topic state
			value = optionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, CHECK_SELECTED_DEFAULT);
			topicEnableUpdate.setSelected(Boolean.valueOf(value));
			toggleTopicChecks(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, CHECK_SELECTED_DEFAULT);
			topicSetCreator.setSelected(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, CHECK_SELECTED_DEFAULT);
			topicSetCreated.setSelected(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, CHECK_SELECTED_DEFAULT);
			topicUpdateContributor.setSelected(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, CHECK_SELECTED_DEFAULT);
			topicUpdateRevised.setSelected(Boolean.valueOf(value));

			// Load DITA map state
			value = optionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, CHECK_SELECTED_DEFAULT);
			mapEnableUpdate.setSelected(Boolean.valueOf(value));
			toggleMapChecks(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, CHECK_SELECTED_DEFAULT);
			mapSetCreator.setSelected(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, CHECK_SELECTED_DEFAULT);
			mapSetCreated.setSelected(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, CHECK_SELECTED_DEFAULT);
			mapUpdateContributor.setSelected(Boolean.valueOf(value));

			value = optionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, CHECK_SELECTED_DEFAULT);
			mapUpdateRevised.setSelected(Boolean.valueOf(value));
		}
	}

	/**
	 * Restore the default state of the PrologOptionPage.
	 */
	public void restoreDefault() {
		// Restore the textField with the author name.
		authorTextField.setText(AUTHOR_DEFAULT);

		// Restore the DITA topic check boxes.
		topicEnableUpdate.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		toggleTopicChecks(CHECK_SELECTED_DEFAULT_BOOLEAN);

		topicSetCreator.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		topicSetCreated.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		topicUpdateContributor.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		topicUpdateRevised.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);

		// Restore the DITA map check boxes.
		mapEnableUpdate.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		toggleMapChecks(CHECK_SELECTED_DEFAULT_BOOLEAN);

		mapSetCreator.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		mapSetCreated.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		mapUpdateContributor.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
		mapUpdateRevised.setSelected(CHECK_SELECTED_DEFAULT_BOOLEAN);
	}
}
