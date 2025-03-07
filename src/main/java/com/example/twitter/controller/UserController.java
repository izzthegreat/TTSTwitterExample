package com.example.twitter.controller;

import com.example.twitter.model.Tweet;
import com.example.twitter.model.User;
import com.example.twitter.service.TweetService;
import com.example.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;


@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private TweetService tweetService;


	@GetMapping(value = "/users/{username}")
	public String getUser(@PathVariable(value = "username") String username, Model model) {
		User user = userService.findByUsername(username);
		List<Tweet> tweets = tweetService.findAllByUser(user);
		User loggedInUser = userService.getLoggedInUser();
		List<User> following = loggedInUser.getFollowing();
		boolean isFollowing = false;
		for (User followedUser : following) {
			if (followedUser.getUsername().equals(username)) {
				isFollowing = true;
			}
		}
		boolean isSelfPage = loggedInUser.getUsername().equals(username);
		model.addAttribute("isSelfPage", isSelfPage);
		model.addAttribute("following", isFollowing);
		model.addAttribute("tweetList", tweets);
		model.addAttribute("user", user);
		return "user";
	}

	@GetMapping(value = "/users")
	public String getUsers(Model model) {
		List<User> users = userService.findAll();
		User loggedInUser = userService.getLoggedInUser();
		List<User> usersFollowing = loggedInUser.getFollowing();
		SetFollowingStatus(users, usersFollowing, model);
		model.addAttribute("users", users);
		SetTweetCounts(users, model);
		return "users";
	}

	private void SetFollowingStatus(List<User> users, List<User> usersFollowing, Model model) {
		HashMap<String,Boolean> followingStatus = new HashMap<>();
		String username = userService.getLoggedInUser().getUsername();
		for (User user : users) {
			if(usersFollowing.contains(user)) {
				followingStatus.put(user.getUsername(), true);
			}else if (!user.getUsername().equals(username)) {
				followingStatus.put(user.getUsername(), false);
			}
		}
		model.addAttribute("followingStatus", followingStatus);
	}

		private void SetTweetCounts(List<User> users, Model model) {
		HashMap<String,Integer> tweetCounts = new HashMap<>();
		for (User user : users) {
			List<Tweet> tweets = tweetService.findAllByUser(user);
			tweetCounts.put(user.getUsername(), tweets.size());
		}
		model.addAttribute("tweetCounts", tweetCounts);
	}
}
