package com.czat;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MessageFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private Contact selectedContact = null;
    private OnListFragmentInteractionListener mListener;
    private Chat app;
    private int oldestMessageId = -1;
    private boolean allMessagesLoaded = false;
    private EditText messageInput;
    private Button sendButton;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageFragment() {

    }

    public void setContact(final Contact contact) {
        Log.d("MessageFragment", "setContact");
        if (contact == null || (selectedContact != null && selectedContact.getId() == contact.getId())) return;
        selectedContact = contact;
        MessageList.clear();
        allMessagesLoaded = false;
        getMessages();
        app.setOnIncomingMessage(new Callback() {
            @Override
            public void run(JSONObject response) {
                try {
                    int sender = response.getInt("sender");
                    if (sender != selectedContact.getId()) return;
                    int mId = response.getInt("id");
                    String content = response.getString("content");
                    Message m = new Message(mId, sender, app.getUserId(), content);
                    m.setSenderUsername(selectedContact.getUsername());
                    MessageList.addItem(m);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.getAdapter().notifyDataSetChanged();
                            mLinearLayoutManager.scrollToPosition(MessageList.ITEMS.size() - 1);
                        }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }

    private void getMessages() {
        if (selectedContact == null || allMessagesLoaded) return;
        app.setOnGetMessages(new Callback() {
            @Override
            public void run(JSONObject response) {
                try {
                    Boolean success = response.getBoolean("success");
                    if (success) {
                        int userId = response.getInt("user_id");
                        if (userId == selectedContact.getId()) {
                            JSONArray messages = response.getJSONArray("messages");
                            final boolean latestMessages = MessageList.ITEMS.size() == 0;
                            for (int i = 0; i < messages.length(); i++) {
                                JSONObject obj = messages.getJSONObject(i);
                                int mId = obj.getInt("id");
                                int sender = obj.getInt("sender");
                                int recipient = obj.getInt("recipient");
                                String content = obj.getString("content");
                                if (mId < oldestMessageId) mId = oldestMessageId;
                                Message m = new Message(mId, sender, recipient, content);
                                m.setSenderUsername(sender == app.getUserId() ? app.getUsername() : selectedContact.getUsername());
                                MessageList.addItem(m);
                            }
                            allMessagesLoaded = !response.getBoolean("more_messages");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                    if (latestMessages) mLinearLayoutManager.scrollToPosition(MessageList.ITEMS.size() - 1);
                                    messageInput.setEnabled(true);
                                    sendButton.setEnabled(true);
                                }
                            });
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
                app.setOnGetMessages(null);
            }
        });
        app.getMessages(selectedContact.getId(), oldestMessageId);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MessageFragment newInstance(int columnCount) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Chat)getActivity().getApplication();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyMessageRecyclerViewAdapter(MessageList.ITEMS, mListener));
        }
        return view;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        View view = getView();
        if (view == null) return;
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        messageInput = (EditText) view.findViewById(R.id.messageInput);
        sendButton = (Button) view.findViewById(R.id.sendButton);
        Button sendButton = (Button) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonClick();
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new MyMessageRecyclerViewAdapter(MessageList.ITEMS, mListener));
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        Contact contact = (Contact) getActivity().getIntent().getSerializableExtra("contact");
        if (contact != null) setContact(contact);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        app.setOnIncomingMessage(null);
        mListener = null;
    }

    public void sendButtonClick() {
        if (selectedContact == null) return;
        app.setOnSendMessageResponse(new Callback() {
            @Override
            public void run(JSONObject response) {
                try {
                    Boolean success = response.getBoolean("success");
                    if (success) {
                        int mId = response.getInt("id");
                        int recipient = response.getInt("recipient");
                        String content = response.getString("content");
                        Message m = new Message(mId, app.getUserId(), recipient, content);
                        m.setSenderUsername(app.getUsername());
                        MessageList.addItem(m);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.getAdapter().notifyDataSetChanged();
                                mLinearLayoutManager.scrollToPosition(MessageList.ITEMS.size() - 1);
                            }
                        });
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
        app.sendMessage(selectedContact.getId(), messageInput.getText().toString());
        messageInput.setText("");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Message item);
    }
}
