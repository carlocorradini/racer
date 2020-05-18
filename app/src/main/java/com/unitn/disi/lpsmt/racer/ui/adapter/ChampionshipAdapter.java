package com.unitn.disi.lpsmt.racer.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.Car;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipCircuit;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipGameSetting;
import com.unitn.disi.lpsmt.racer.api.entity.Circuit;
import com.unitn.disi.lpsmt.racer.api.entity.GameSetting;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * {@link Championship} {@link BaseExpandableListAdapter Adapter}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipAdapter extends BaseExpandableListAdapter {

    /**
     * The number of groups
     */
    private static final int GROUP_TYPE_COUNT = 4;
    /**
     * The number of group's child
     */
    private static final int CHILD_TYPE_COUNT = 4;
    /**
     * Invalid group
     */
    private static final int GROUP_TYPE_UNKNOWN = -1;
    /**
     * Invalid child
     */
    private static final int CHILD_TYPE_UNKNOWN = -1;
    /**
     * {@link UserChampionship} type
     */
    public static final int TYPE_CHAMPIONSHIP_USER = 0;
    /**
     * {@link ChampionshipCircuit} type
     */
    public static final int TYPE_CHAMPIONSHIP_CIRCUIT = 1;
    /**
     * {@link Car} type
     */
    public static final int TYPE_CHAMPIONSHIP_CAR = 2;
    /**
     * {@link ChampionshipGameSetting} type
     */
    public static final int TYPE_CHAMPIONSHIP_GAME_SETTING = 3;

    /**
     * Current {@link Context}
     */
    private Context context;
    /**
     * {@link Map} of expandable date.
     * Key is the type identifier.
     * Value is the List of {@link Object} key
     */
    private Map<Integer, List<Object>> expandableListData;

    /**
     * Construct a new {@link ChampionshipAdapter} given the context and the data
     *
     * @param context            The current {@link Context}
     * @param expandableListData The data to represent
     */
    public ChampionshipAdapter(Context context, Map<Integer, List<Object>> expandableListData) {
        this.context = context;
        this.expandableListData = expandableListData;
    }

    /**
     * Set the data dynamically given the groupPosition as key and listData as value
     *
     * @param groupPosition The group key type
     * @param listData      The data to map given the groupPosition key
     */
    public void setListData(int groupPosition, List<Object> listData) {
        expandableListData.put(groupPosition, listData);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(expandableListData.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(expandableListData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return GROUP_TYPE_COUNT;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getChildTypeCount() {
        return CHILD_TYPE_COUNT;
    }

    @Override
    public int getGroupTypeCount() {
        return GROUP_TYPE_COUNT;
    }

    @Override
    public int getGroupType(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return TYPE_CHAMPIONSHIP_USER;
            case 1:
                return TYPE_CHAMPIONSHIP_CIRCUIT;
            case 2:
                return TYPE_CHAMPIONSHIP_CAR;
            case 3:
                return TYPE_CHAMPIONSHIP_GAME_SETTING;
            default:
                return GROUP_TYPE_UNKNOWN;
        }
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                return TYPE_CHAMPIONSHIP_USER;
            case 1:
                return TYPE_CHAMPIONSHIP_CIRCUIT;
            case 2:
                return TYPE_CHAMPIONSHIP_CAR;
            case 3:
                return TYPE_CHAMPIONSHIP_GAME_SETTING;
            default:
                return CHILD_TYPE_UNKNOWN;
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @DrawableRes int titleIcon = 0;
        @StringRes int titleText = 0;
        TextView txtTitle;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_championship_list_group, null);
        }

        txtTitle = convertView.findViewById(R.id.fragment_championship_list_group_title);

        switch (getGroupType(groupPosition)) {
            case TYPE_CHAMPIONSHIP_USER: {
                titleIcon = R.drawable.ic_pilots_formula_one_color;
                titleText = R.string.pilots;
                break;
            }
            case TYPE_CHAMPIONSHIP_CIRCUIT: {
                titleIcon = R.drawable.ic_circuit_formula_one_color;
                titleText = R.string.circuits;
                break;
            }
            case TYPE_CHAMPIONSHIP_CAR: {
                titleIcon = R.drawable.ic_car_formula_one_color;
                titleText = R.string.cars;
                break;
            }
            case TYPE_CHAMPIONSHIP_GAME_SETTING: {
                titleIcon = R.drawable.ic_settings_color;
                titleText = R.string.settings;
                break;
            }
        }

        txtTitle.setCompoundDrawablesWithIntrinsicBounds(titleIcon, 0, 0, 0);
        txtTitle.setText(titleText);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final int childType = getChildType(groupPosition, childPosition);

        if (convertView == null) {
            switch (childType) {
                case TYPE_CHAMPIONSHIP_USER: {
                    convertView = inflater.inflate(R.layout.fragment_championship_list_child_user, null);
                    break;
                }
                case TYPE_CHAMPIONSHIP_CIRCUIT: {
                    convertView = inflater.inflate(R.layout.fragment_championship_list_child_circuit, null);
                    break;
                }
                case TYPE_CHAMPIONSHIP_CAR: {
                    convertView = inflater.inflate(R.layout.fragment_championship_list_child_car, null);
                    break;
                }
                case TYPE_CHAMPIONSHIP_GAME_SETTING: {
                    convertView = inflater.inflate(R.layout.fragment_championship_list_child_game_setting, null);
                    break;
                }
            }
        }

        switch (childType) {
            case TYPE_CHAMPIONSHIP_USER: {
                Triple<UserChampionship, User, Car> userChampionships = (Triple<UserChampionship, User, Car>) getChild(groupPosition, childPosition);
                TextView txtUsername = convertView.findViewById(R.id.fragment_championship_list_child_user_username);
                TextView txtCar = convertView.findViewById(R.id.fragment_championship_list_child_user_car);

                txtUsername.setText(userChampionships.getMiddle().username);
                txtCar.setText(userChampionships.getRight().getFullName());
                break;
            }
            case TYPE_CHAMPIONSHIP_CIRCUIT: {
                Pair<ChampionshipCircuit, Circuit> championshipCircuits = (Pair<ChampionshipCircuit, Circuit>) getChild(groupPosition, childPosition);
                TextView txtDate = convertView.findViewById(R.id.fragment_championship_list_child_circuit_date);
                TextView txtCircuitName = convertView.findViewById(R.id.fragment_championship_list_child_circuit_name);

                txtDate.setText(championshipCircuits.getLeft().date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                txtCircuitName.setText(championshipCircuits.getRight().name);
                break;
            }
            case TYPE_CHAMPIONSHIP_CAR: {
                Car car = (Car) getChild(groupPosition, childPosition);

                TextView txtId = convertView.findViewById(R.id.fragment_championship_list_child_car_id);
                TextView txtName = convertView.findViewById(R.id.fragment_championship_list_child_car_name);

                txtId.setText(String.valueOf(car.id));
                txtName.setText(car.getFullName());
                break;
            }
            case TYPE_CHAMPIONSHIP_GAME_SETTING: {
                Pair<ChampionshipGameSetting, GameSetting> championshipGameSetting = (Pair<ChampionshipGameSetting, GameSetting>) getChild(groupPosition, childPosition);
                TextView txtName = convertView.findViewById(R.id.fragment_championship_list_child_game_setting_name);
                TextView txtValue = convertView.findViewById(R.id.fragment_championship_list_child_game_setting_value);

                txtName.setText(StringUtils.capitalize(championshipGameSetting.getRight().name));
                txtValue.setText(StringUtils.capitalize(championshipGameSetting.getLeft().value));

                break;
            }
        }

        return convertView;
    }
}
