package com.example.twitter.controller;

import com.example.twitter.model.Tweet;
import com.example.twitter.model.User;
import com.example.twitter.service.TweetService;
import com.example.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class TweetController {
	@Autowired
	private UserService userService;

	@Autowired
	private TweetService tweetService;

	@GetMapping(value = {"/tweets", "/"})
	public String getFeed(Model model) {
		List<Tweet> tweets = tweetService.findAll();
		model.addAttribute("tweetList", tweets);
		return "feed";
	}

	@GetMapping(value = "/tweets/new")
	public String getTweetForm(Model model) {
		model.addAttribute("tweet", new Tweet());
		return "newTweet";
	}

	@PostMapping(value = "/tweets")
	public String submitTweetForm(@Valid Tweet tweet, BindingResult bindingResult, Model model) {
		User user = userService.getLoggedInUser();
		if (!bindingResult.hasErrors()) {
			tweet.setUser(user);
			tweetService.save(tweet);
			model.addAttribute("successMessage", "Tweet successfully created!");
			model.addAttribute("tweet", new Tweet());
		}
		return "newTweet";
	}

	@GetMapping(value = "/tweets/{tag}")
	public String getTweetsByTag(@PathVariable(value="tag") String tag, Model model) {
		List<Tweet> tweets = tweetService.findAllWithTag(tag);
		model.addAttribute("tweetList", tweets);
		model.addAttribute("tag", tag);
		return "taggedTweets";
	}
}